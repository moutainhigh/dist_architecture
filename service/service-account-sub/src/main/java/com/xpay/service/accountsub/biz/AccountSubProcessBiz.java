package com.xpay.service.accountsub.biz;

import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.enums.account.*;
import com.xpay.common.statics.exception.AccountSubExceptions;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.MD5Util;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountsub.dto.AccountSubProcessDetailDto;
import com.xpay.facade.accountsub.dto.AccountSubProcessDto;
import com.xpay.facade.accountsub.dto.AccountSubRequestDto;
import com.xpay.facade.accountsub.entity.AccountSub;
import com.xpay.facade.accountsub.entity.AccountSubProcessPending;
import com.xpay.facade.accountsub.entity.AccountSubProcessResult;
import com.xpay.service.accountsub.accounting.AccountingHelper;
import com.xpay.service.accountsub.accounting.AccountingProcessor;
import com.xpay.service.accountsub.dao.AccountSubDao;
import com.xpay.service.accountsub.dao.AccountSubProcessDetailDao;
import com.xpay.service.accountsub.dao.AccountSubProcessDetailHistoryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Author: chenyf
 * Date: 2019.12.18
 * Time: 18:40
 * Description:账务处理业务逻辑层
 */
@Component
public class AccountSubProcessBiz {
    private Logger logger = LoggerFactory.getLogger(AccountSubProcessBiz.class);

    private final static String ACCOUNT_PENDING_UNIQUE_TABLE_NAME = "tbl_account_sub_process_pending";
    private final static String ACCOUNT_UNIQUE_TABLE_NAME = "tbl_account_sub_common_unique";

    @Autowired
    AccountSubDao accountSubDao;
    @Autowired
    AccountSubProcessDetailDao accountSubProcessDetailDao;
    @Autowired
    AccountSubProcessDetailHistoryDao accountSubProcessDetailHistoryDao;

    @Autowired
    AccountSubProcessPendingBiz accountSubProcessPendingBiz;
    @Autowired
    AccountSubProcessResultBiz accountSubProcessResultBiz;
    @Autowired
    AccountingHelper accountingHelper;

    /**
     * 异步账务处理
     *
     * @param requestDto     .
     * @param processDtoList .
     * @return 返回插入待账务处理记录的数据库ID
     */
    public Long executeAsync(AccountSubRequestDto requestDto, List<AccountSubProcessDto> processDtoList) {
        String accountProcessNo = requestDto.getAccountProcessNo();
        StringBuffer uniqueKeyFlag = new StringBuffer();

        for (AccountSubProcessDto processDto : processDtoList) {
            uniqueKeyFlag.append(processDto.getMerchantNo()).append(processDto.getTrxNo()).append(processDto.getProcessType());
            if (requestDto.getDataUnqKeyWithAmountType()) {
                uniqueKeyFlag.append(processDto.getAmountType());
            }
        }
        String dataUnqKey = MD5Util.getMD5Hex(uniqueKeyFlag.toString());

        AccountSubProcessPending processPending = new AccountSubProcessPending();
        processPending.setCreateTime(new Date());
        processPending.setVersion(0);
        processPending.setModifyTime(processPending.getCreateTime());
        processPending.setAccountProcessNo(accountProcessNo);
        processPending.setProcessStage(AccountProcessPendingStageEnum.PENDING.getValue());
        processPending.setDataUnqKey(dataUnqKey);
        processPending.setRequestDto(JsonUtil.toStringFriendlyNotNull(requestDto));
        processPending.setProcessDtoList(JsonUtil.toStringFriendlyNotNull(processDtoList));
        processPending.setRemark("");//预留

        try {
            accountSubProcessPendingBiz.add(processPending);
            return processPending.getId();
        } catch (Throwable e) {
            logger.error("==>executeAsync accountProcessNo={} 待账务处理入库出现异常", accountProcessNo, e);

            if (this.isPendingRecordDataRepeat(e)) {
                throw AccountSubExceptions.ACCOUNT_PROCESS_PENDING_UNIQUE_KEY_REPEAT.newWithErrMsg("异步账务处理记录已存在");
            } else if (this.isPendingRecordProcessNoRepeat(e)) {
                throw AccountSubExceptions.ACCOUNT_PROCESS_PENDING_PROCESS_NO_REPEAT.newWithErrMsg("异步账务处理的账务处理流水号重复");
            } else {
                logger.error("执行子商户异步账务处理时出现异常,accountProcessNo={}", requestDto.getAccountProcessNo(), e);
                throw CommonExceptions.UNEXPECT_ERROR.newWithErrMsg(e.getMessage());
            }
        }
    }

    /**
     * 同步账务处理
     *
     * @param requestDto             .
     * @param mchNoMapProcessDtoList .
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean executeSync(AccountSubRequestDto requestDto, Map<String, List<AccountSubProcessDto>> mchNoMapProcessDtoList) {
        for (Map.Entry<String, List<AccountSubProcessDto>> entry : mchNoMapProcessDtoList.entrySet()) {
            //1.账户存在性校验
            AccountSub account = accountSubDao.getByMerchantNo(entry.getKey());
            if (account == null) {
                throw AccountSubExceptions.ACCOUNT_RECORD_NOT_EXIT.newWithErrMsg("子商户账户不存在");
            }
            //2.执行针对每一个账务处理DTO进行账务处理
            for (AccountSubProcessDto processDto : entry.getValue()) {
                this.doProcessDto(account, requestDto, processDto);
            }
            //3.最后更新子商户账户
            logger.info("accountProcessNo={} merchantNo={} version={} 开始更新子商户账户", requestDto.getAccountProcessNo(), account.getMerchantNo(), account.getVersion());
            accountSubDao.update(account);
        }
        return true;
    }

    private void doProcessDto(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        AccountingProcessor accountingProcessor = accountingHelper.adaptProcessor(requestDto.getAccountProcessNo(), processDto);
        try {
            accountingProcessor.process(account, requestDto, processDto);
        } catch (Throwable ex) {
            logger.error("子商户账务处理过程出现异常 processNo={} trxNo={}", requestDto.getAccountProcessNo(), processDto.getTrxNo(), ex);
            if (this.isAccountProcessRepeat(ex)) {
                //如果是重复账务处理，则抛出重复账务处理的异常(不能用continue继续处理下一笔，因为Account、AccountAdvance可能已经加、减过了)
                throw AccountSubExceptions.ACCOUNT_PROCESS_REPEAT.newWithErrMsg(processDto.getTrxNo() + "-重复账务处理");
            } else {
                throw ex;
            }
        }
    }

    /**
     * 同步账务处理前需要处理的逻辑
     *
     * @param requestDto     .
     * @param processDtoList .
     */
    public void beforeSyncExecute(AccountSubRequestDto requestDto, List<AccountSubProcessDto> processDtoList, boolean isRevert) {
        if (requestDto.getFromAsync()) {
            if (isRevert) {
                //更新待处理表状态回退到"待处理"
                accountSubProcessPendingBiz.updatePendingStatus(requestDto.getAccountProcessNo(), AccountProcessPendingStageEnum.PENDING, AccountProcessPendingStageEnum.PROCESSING);
            } else {
                //更新待处理表状态为"处理中"
                accountSubProcessPendingBiz.updatePendingStatus(requestDto.getAccountProcessNo(), AccountProcessPendingStageEnum.PROCESSING, AccountProcessPendingStageEnum.PENDING);
            }
        }
    }

    /**
     * 同步账务处理后需要处理的逻辑
     *
     * @param requestDto     .
     * @param processDtoList .
     * @param isSuccess      .
     * @param ex             .
     * @return 账务处理结果数据库id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long afterSyncExecute(AccountSubRequestDto requestDto, List<AccountSubProcessDto> processDtoList, boolean isSuccess, Throwable ex) {
        Integer errorCode = getErrorCode(ex);
        String errorMsg = getErrorMsg(ex);

        List<String> trxNos = new ArrayList<>(processDtoList.size());
        for (AccountSubProcessDto processDto : processDtoList) {
            trxNos.add(processDto.getTrxNo());
            processDto.setDebitDetailDto(null);
        }
        //打印到日志中，方便有问题时查找到日志进行数据恢复
        logger.info("isSuccess={} isFromAsync={} errorCode={} errorMsg={} trxNos={} requestDto={}", isSuccess,
                requestDto.getFromAsync(), errorCode, errorMsg, JsonUtil.toString(trxNos), JsonUtil.toString(requestDto));
        //审核阶段：1=不审核 2=待审核 3=已审核；只有异步账务处理并且是系统异常时才需要审核
        int auditStage;
        if (this.isNeedAudit(isSuccess, requestDto.getFromAsync(), errorCode)) {
            auditStage = AccountProcessResultAuditStageEnum.AUDIT_WAITING.getValue();
        } else {
            auditStage = AccountProcessResultAuditStageEnum.AUDIT_NONE.getValue();
        }

        AccountSubProcessResult accountProcessResult = new AccountSubProcessResult();
        accountProcessResult.setCreateTime(new Date());
        accountProcessResult.setAccountProcessNo(requestDto.getAccountProcessNo());
        accountProcessResult.setProcessResult(isSuccess ? PublicStatus.ACTIVE : PublicStatus.INACTIVE);
        accountProcessResult.setErrorCode(errorCode);
        accountProcessResult.setAuditStage(auditStage);
        accountProcessResult.setCallbackStage(AccountProcessResultCallbackStageEnum.PENDING.getValue());
        accountProcessResult.setRemark(errorMsg);
        accountProcessResult.setIsFromAsync(requestDto.getFromAsync() ? PublicStatus.ACTIVE : PublicStatus.INACTIVE);
        accountProcessResult.setRequestDto(JsonUtil.toStringFriendlyNotNull(requestDto));
        accountProcessResult.setProcessDtoList(JsonUtil.toStringFriendlyNotNull(processDtoList));

        //如果是异步处理的，则把待账物处理表的状态更新为"已处理"
        if (requestDto.getFromAsync()) {
            accountSubProcessPendingBiz.updatePendingStatus(requestDto.getAccountProcessNo(), AccountProcessPendingStageEnum.FINISHED, AccountProcessPendingStageEnum.PROCESSING);
        }

        //写入账务处理结果表
        accountSubProcessResultBiz.add(accountProcessResult);

        return accountProcessResult.getId();
    }

    /**
     * 针对退回的账务处理请求，查询扣款时的账务明细，并过滤掉已经退回过的vo
     *
     * @param processDtoList .
     */
    public void findDebitDetailAndFilterRepeatVo(List<AccountSubProcessDto> processDtoList) {
        ListIterator<AccountSubProcessDto> iteratorList = processDtoList.listIterator();
        while (iteratorList.hasNext()) {
            AccountSubProcessDto processDto = iteratorList.next();
            if (processDto.getProcessType() == AccountProcessTypeEnum.RETURN.getValue()) {
                AccountSubProcessDetailDto debitDetailDto = accountSubProcessDetailDao.getDebitDetailDtoByMchNoAndTrxNo(processDto.getMerchantNo(), processDto.getTrxNo());
                if (debitDetailDto == null) {
                    logger.error("退回账务处理,找不到对应的扣款明细,processDto={}", JsonUtil.toString(processDto));
                } else if (debitDetailDto.getDebitCommitStage() == AccountDebitCommitStageEnum.DEBIT_COMMITTED.getValue()) {
                    logger.error("退回账务处理，扣款账务明细的扣款确认状态为“已确认扣款”,processDto={}", JsonUtil.toString(processDto));
                } else if (debitDetailDto.getDebitCommitStage() == AccountDebitCommitStageEnum.RETURN_COMMITTED.getValue()) {
                    logger.info("退回账务处理，扣款账务明细的扣款确认状态为“已确认退回”,无须重复处理,processDto={}", JsonUtil.toString(processDto));
                    iteratorList.remove();
                } else {
                    processDto.setDebitDetailDto(debitDetailDto);
                }
            }
        }
    }

    /**
     * 是否重复账务处理
     *
     * @param e .
     * @return .
     */
    private boolean isAccountProcessRepeat(Throwable e) {
        if (e != null) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains(ACCOUNT_UNIQUE_TABLE_NAME) && errorMsg.contains("Duplicate entry")) {
                return true;
            } else if (e instanceof BizException && ((BizException) e).getSysErrorCode() == AccountSubExceptions.ACCOUNT_PROCESS_REPEAT.getSysErrorCode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断待账务处理记录的数据是否重复
     *
     * @param e
     * @return
     */
    private boolean isPendingRecordDataRepeat(Throwable e) {
        if (e != null) {
            String errorMsg = e.getMessage();
            if (e instanceof DuplicateKeyException && errorMsg != null && errorMsg.contains(ACCOUNT_PENDING_UNIQUE_TABLE_NAME)
                    && errorMsg.contains("Duplicate entry") && errorMsg.contains("for key 'UK_DATA_UNQ_KEY'")) {
                return true;//是AccountProcessVo中的数据重复了
            }
        }
        return false;
    }

    /**
     * 判断待账务处理记录的账务处理流水号是否重复
     *
     * @param e .
     * @return .
     */
    private boolean isPendingRecordProcessNoRepeat(Throwable e) {
        if (e != null) {
            String errorMsg = e.getMessage();
            if (e instanceof DuplicateKeyException && errorMsg != null && errorMsg.contains(ACCOUNT_PENDING_UNIQUE_TABLE_NAME)
                    && errorMsg.contains("Duplicate entry") && errorMsg.contains("for key 'ACCOUNT_PROCESS_NO'")) {
                return true;//账务处理流水号重复了
            }
        }
        return false;
    }

    /**
     * 根据异常信息获取错误码
     *
     * @param ex
     * @return
     */
    private int getErrorCode(Throwable ex) {
        if (ex == null) {
            //无异常
            return 0;
        } else if (ex instanceof BizException) {
            //业务异常
            return ((BizException) ex).getSysErrorCode();
        } else {
            //系统异常
            return 1;
        }
    }

    /**
     * 根据异常信息获取错误描述信息
     *
     * @param ex
     * @return
     */
    private String getErrorMsg(Throwable ex) {
        if (ex == null) {
            //无异常
            return "";
        } else if (ex instanceof BizException) {
            //业务异常
            return ((BizException) ex).getErrMsg();
        } else {
            //系统异常，截取前500个字符，因为数据库中只保存500
            String msg = ex.toString();
            return StringUtil.subLeft(msg, 500);
        }
    }

    /**
     * 账务处理结果是否需要审核
     *
     * @param isSuccess   .
     * @param isFromAsync .
     * @param errorCode   .
     * @return
     */
    private boolean isNeedAudit(boolean isSuccess, boolean isFromAsync, int errorCode) {
        if (isSuccess || !isFromAsync) {
            //处理成功、同步账务处理，都不需要审核
            return false;
        } else {
            if (errorCode == AccountSubExceptions.USABLE_AMOUNT_NOT_ENOUGH.getSysErrorCode()
                    || errorCode == AccountSubExceptions.UNSETTLE_AMOUNT_NOT_ENOUGH.getSysErrorCode()) {
                //对于各种余额不足的业务异常，不需要审核
                return false;
            } else if (errorCode > 0) {
                //其他异常，一律要审核，其中，errorCode等于1时，表示系统异常，其他值则表示业务异常
                return true;
            } else {
                return false;
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void commitDebitSync(List<String[]> merchantNoAndTrxNoList) {
        int updateRows = accountSubProcessDetailDao.updateDebitCommitStage(merchantNoAndTrxNoList, AccountDebitCommitStageEnum.UN_COMMITTED, AccountDebitCommitStageEnum.DEBIT_COMMITTED);
        if (updateRows == 0) {
            //如果等于0，则有可能已经修改过，查询状态
            long committedRows = accountSubProcessDetailDao.countDebitCommitDetail(merchantNoAndTrxNoList, AccountDebitCommitStageEnum.DEBIT_COMMITTED);
            if (committedRows == merchantNoAndTrxNoList.size()) {
                logger.info("扣款明细被判断为已经确认扣款状态,merchantNoAndTrxNoList={}", JsonUtil.toString(merchantNoAndTrxNoList));
                return;
            } else {
                logger.error("扣款明细已被确认扣款状态的数目与请求数目不一致,committedRows={},trxNoList={}", committedRows, JsonUtil.toString(merchantNoAndTrxNoList));
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("确认账务扣款失败，修改成功数与预期数量不一致");
            }
        } else if (updateRows != merchantNoAndTrxNoList.size()) {
            logger.error("确认账务扣款失败，修改成功数与预期数量不一致,updateRows={},trxNoList={}", updateRows, JsonUtil.toString(merchantNoAndTrxNoList));
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("确认账务扣款失败，修改成功数与预期数量不一致");
        }
    }
}
