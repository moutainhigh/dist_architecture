package com.xpay.service.accounttransit.biz;

import com.xpay.common.statics.constants.common.DistLockConst;
import com.xpay.common.statics.constants.rmqdest.AccountMsgDest;
import com.xpay.common.statics.dto.accounttransit.AccountTransitAmountTypeEnum;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.statics.enums.product.BusinessTypeEnum;
import com.xpay.common.statics.exception.AccountTransitExceptions;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.LockNameUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessPending;
import com.xpay.starter.comp.component.RMQSender;
import com.xpay.starter.comp.component.RedisLock;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: chenyf
 * Date: 2019.12.18
 * Time: 18:38
 * Description:账务处理器
 */
@Component
public class AccountTransitProcessHandler {
    private Logger logger = LoggerFactory.getLogger(AccountTransitProcessHandler.class);
    //每次账务处理允许的商户数
    private final static int MAX_USER_COUNT_PER_PROCESS = 2;
    //每次批量账务处理允许个数
    private final static int MAX_PROCESS_COUNT_PER_PROCESS = 50;

    @Autowired
    private AccountTransitProcessBiz accountTransitProcessBiz;
    @Autowired
    AccountTransitProcessPendingBiz accountTransitProcessPendingBiz;
    @Autowired
    AccountTransitProcessResultBiz accountTransitProcessResultBiz;

    @Autowired
    private RMQSender rmqSender;
    @Autowired
    RedisLock redisClient;

    /**
     * 同步账务处理
     *
     * @param requestDto .
     * @param processDto .
     */
    public boolean executeSync(AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) throws BizException {
        List<AccountTransitProcessDto> processVoList = new ArrayList<>(1);
        processVoList.add(processDto);
        requestDto.setFromAsync(false);
        return this.executeSync(requestDto, processVoList);
    }

    /**
     * 同步批量账务处理
     *
     * @param requestDto     .
     * @param processDtoList .
     */
    public boolean executeSync(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) throws BizException {
        logger.info("requestDto={} processDtoList={} ", JsonUtil.toString(requestDto), JsonUtil.toString(processDtoList));

        //1.参数校验
        this.validAccountProcessParam(requestDto, processDtoList);

        String accountProcessNo = requestDto.getAccountProcessNo();
        //2.如果是退回，需要找到之前的扣款账务明细
        accountTransitProcessBiz.findDebitDetailAndFilterRepeatVo(processDtoList);
        if (processDtoList.isEmpty()) {
            logger.info("accountProcessNo={} 全部均已经账务处理过，无需重复处理！", accountProcessNo);
            throw AccountTransitExceptions.ACCOUNT_PROCESS_REPEAT.newWithErrMsg("请勿重复账务处理");
        }

        //3、按用户分组
        Map<String, List<AccountTransitProcessDto>> groupMap = processDtoList.stream().collect(Collectors.groupingBy(AccountTransitProcessDto::getMerchantNo));

        //4.账务处理前置处理
        try {
            accountTransitProcessBiz.beforeSyncExecute(requestDto, processDtoList, false);
        } catch (Throwable e) {
            logger.error("accountProcessNo={} 账务处理前发生异常", accountProcessNo, e);
            throwBizExceptionIfNecessary(e);
            return false;
        }

        //5、给所有账户都加锁
        List<RLock> lockList = null;
        try {
            logger.info("accountProcessNo={} 在途账户获取锁开始", accountProcessNo);
            lockList = redisClient.tryLock(LockNameUtil.getAccountTransitLockName(groupMap.keySet()), DistLockConst.ACCOUNT_LOCK_WAIT_MILLS, DistLockConst.ACCOUNT_LOCK_EXPIRE_MILLS);
            logger.info("accountProcessNo={} 在途账户获取锁结束 lockList.size={}", accountProcessNo, lockList.size());
        } catch (Throwable t) {
            logger.error("accountProcessNo={} groupMap.size={} 在途账户获取锁出现异常", accountProcessNo, groupMap.size(), t);
        } finally {
            if (lockList == null || lockList.size() != groupMap.size()) {
                logger.error("accountProcessNo={} groupMap.size={} lockList.size={} 在途账户获取锁失败", accountProcessNo, groupMap.size(), lockList == null ? 0 : lockList.size());
                try {
                    //回退到'待处理'
                    accountTransitProcessBiz.beforeSyncExecute(requestDto, processDtoList, true);
                } catch (Throwable e) {
                    logger.error("accountProcessNo={} 在途账户待账务处理回退到'待处理'时发生异常", accountProcessNo, e);
                    throwBizExceptionIfNecessary(e);
                }
                throw AccountTransitExceptions.ACQUIRE_LOCK_FAIL.newWithErrMsg("获取在途账户锁失败");
            }
        }

        //6.账务处理中
        boolean isSuccess = false;
        Throwable exProcessing = null;
        try {
            //方法内部还有进一步做重复账务处理检测
            isSuccess = accountTransitProcessBiz.executeSync(requestDto, groupMap);
        } catch (Throwable ex) {
            exProcessing = ex;
            logger.error("accountProcessNo={} 在途账户账务处理过程发生异常！", accountProcessNo, exProcessing);
        } finally {
            //释放锁
            try {
                redisClient.unlock(lockList);
            } catch (Throwable ex) {
                logger.error("释放redis锁时出现异常,accountProcessNo={}", accountProcessNo, ex);
            }
        }

        //7.账务处理后
        Long processResultId = null;
        try {
            processResultId = accountTransitProcessBiz.afterSyncExecute(requestDto, processDtoList, isSuccess, exProcessing);
        } catch (Throwable exAfter) {
            logger.error("accountProcessNo={} 在途账户账务处理后发生异常", accountProcessNo, exAfter);
        }

        //8.如果需要回调并且是加急回调，则通过MQ通知立即执行账务处理回调
        if (requestDto.getUrgent()) {
            this.notifyDoAccountProcessCallBack(requestDto.getAccountProcessNo(), processResultId);  //todo scan
        }
        throwBizExceptionIfNecessary(exProcessing);
        return isSuccess;
    }

    /**
     * 异步之后的同步账务处理
     *
     * @param accountProcessPendingId 待账务处理id
     */
    public boolean executeSyncForAsync(long accountProcessPendingId) {
        logger.info("==>executeSyncForAsync accountProcessPendingId={}", accountProcessPendingId);
        AccountTransitProcessPending processPending = accountTransitProcessPendingBiz.getAccountProcessPendingById(accountProcessPendingId, false);
        if (processPending == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("accountProcessPendingId=" + accountProcessPendingId + "待账务处理记录不存在");
        } else if (processPending.getProcessStage() != AccountProcessPendingStageEnum.PENDING.getValue()) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("accountProcessPendingId=" + accountProcessPendingId + "待账务处理记录已处理，不可重复处理");
        }

        AccountTransitRequestDto requestDto = JsonUtil.toBean(processPending.getRequestDto(), AccountTransitRequestDto.class);
        List<AccountTransitProcessDto> processDtoList = JsonUtil.toList(processPending.getProcessDtoList(), AccountTransitProcessDto.class);
        return this.executeSync(requestDto, processDtoList);
    }

    /**
     * 异步账务处理
     *
     * @param requestDto .
     * @param processDto .
     */
    public boolean executeAsync(AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        List<AccountTransitProcessDto> processDtoList = new ArrayList<>(1);
        processDtoList.add(processDto);
        return this.executeAsync(requestDto, processDtoList);
    }

    /**
     * 异步批量账务处理
     *
     * @param requestDto     .
     * @param processDtoList .
     */
    public boolean executeAsync(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) {
        logger.info("==>executeAsync requestDto={} processDtoList={}", JsonUtil.toString(requestDto), JsonUtil.toString(processDtoList));
        //1.参数校验
        this.validAccountProcessParam(requestDto, processDtoList);
        requestDto.setFromAsync(true);//设置为true

        //2.执行异步账务处理
        Long processPendingId = accountTransitProcessBiz.executeAsync(requestDto, processDtoList);

        //3.如果需要加急账务处理，则发送一条消息通知立即执行账务处理
        if (requestDto.getUrgent()) {
            this.notifyDoAccountProcess(requestDto.getAccountProcessNo(), processPendingId);
        }
        return true;
    }


    /**
     * 确认出款成功
     *
     * @param merchantNoAndTrxNoList 商户号和扣款的平台流水号集合，约定每个数组【0】为商户号，【1】为平台流水号
     * @return
     */
    public void commitDebitSync(List<String[]> merchantNoAndTrxNoList) {
        logger.info("确认出款成功,trxNoList={}", JsonUtil.toString(merchantNoAndTrxNoList));
        if (merchantNoAndTrxNoList == null || merchantNoAndTrxNoList.size() == 0
                || merchantNoAndTrxNoList.stream().anyMatch(p -> p == null || p.length != 2 || StringUtil.isEmpty(p[0]) || StringUtil.isEmpty(p[1]))) {
            logger.error("merchantNoAndTrxNoList参数有误,merchantNoAndTrxNoList={}", JsonUtil.toString(merchantNoAndTrxNoList));
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNoAndTrxNoList参数有误");
        }
        accountTransitProcessBiz.commitDebitSync(merchantNoAndTrxNoList);
    }

    /**
     * 校验账务处理Vo对象参数，如果有校验不通过的地方，直接抛异常
     *
     * @param requestDto     .
     * @param processDtoList .
     */
    private void validAccountProcessParam(AccountTransitRequestDto requestDto, List<AccountTransitProcessDto> processDtoList) {
        if (requestDto == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("requestDto不能为null");
        } else if (StringUtil.isEmpty(requestDto.getAccountProcessNo())) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("requestDto.accountProcessNo不能为空");
        } else if (!requestDto.getAccountProcessNo().matches("[A-Za-z0-9_]{15,30}")) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("requestDto.accountProcessNo只能由字母、数字或下划线组成,长度15--30");
        }

        //校验账务处理的业务对象是否为空
        if (processDtoList == null || processDtoList.isEmpty()) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("processDtoList不能为空");
        } else if (processDtoList.size() > MAX_PROCESS_COUNT_PER_PROCESS) {
            //避免一次账务处理的时间过长
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("processDtoList个数不能超过" + MAX_PROCESS_COUNT_PER_PROCESS);
        } else if (processDtoList.stream().map(AccountTransitProcessDto::getMerchantNo).collect(Collectors.toSet()).size() > MAX_USER_COUNT_PER_PROCESS) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("processDtoList中MerchantNo的个数不能超过" + MAX_USER_COUNT_PER_PROCESS);
        }

        for (AccountTransitProcessDto dto : processDtoList) {
            if (dto.getTrxTime() == null) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("trxTime不能为空");
            } else if (StringUtil.isEmpty(dto.getTrxNo())) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("trxNo不能为空");
            } else if (dto.getProcessType() == null
                    || Arrays.stream(AccountProcessTypeEnum.values()).noneMatch(p -> p.getValue() == dto.getProcessType())) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("未识别的processType" + dto.getProcessType());
            } else if (dto.getAmountType() == null
                    || Arrays.stream(AccountTransitAmountTypeEnum.values()).noneMatch(p -> p.getValue() == dto.getAmountType())) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("未识别的amountType" + dto.getAmountType());
            } else if (StringUtil.isEmpty(dto.getMerchantNo())) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
            } else if (dto.getAmount() == null || AmountUtil.lessThan(dto.getAmount(), BigDecimal.ZERO)) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("amount不能小于0");
            } else if (dto.getBizType() == null
                    || Arrays.stream(BusinessTypeEnum.values()).noneMatch(p -> p.getValue() == dto.getBizType())) {
                throw CommonExceptions.PARAM_INVALID.newWithErrMsg("未识别的bizType" + dto.getBizType());
            }

            //处理默认值
            if (dto.getMchTrxNo() == null) {
                dto.setMchTrxNo("");
            }
            if (dto.getDesc() == null) {
                dto.setDesc("");
            }
        }

    }

    /**
     * 异常抛出转换
     *
     * @param ex .
     */
    private void throwBizExceptionIfNecessary(Throwable ex) {
        if (ex != null) {
            if (ex instanceof BizException) {
                throw (BizException) ex;
            } else {
                throw CommonExceptions.UNEXPECT_ERROR.newWithErrMsg(ex.getMessage());
            }
        }
    }

    /**
     * 通知进行账务处理
     *
     * @param accountProcessNo 账务处理流水号
     * @param processPendingId 待账务处理记录的数据库id
     */
    private void notifyDoAccountProcess(String accountProcessNo, long processPendingId) {
        logger.info("accountProcessNo={},processPendingId={}", accountProcessNo, processPendingId);
        try {
            MsgDto msgDto = new MsgDto();
            msgDto.setTopic(AccountMsgDest.TOPIC_ACCOUNT_TRANSIT_PROCESS);
            msgDto.setTags(AccountMsgDest.TAG_ACCOUNT_TRANSIT_PROCESS_ASYNC);
            msgDto.setTrxNo(accountProcessNo);
            msgDto.setJsonParam(JsonUtil.toString(Collections.singletonMap("processPendingId", processPendingId)));
            boolean isSuccess = rmqSender.sendOne(msgDto);
            logger.info("accountProcessNo={} processPendingId={} isSuccess={} 通知进行账务处理完毕", accountProcessNo, processPendingId, isSuccess);
        } catch (Throwable e) {
            logger.error("accountProcessNo={} processPendingId={} 通知进行账务处理时出现异常", accountProcessNo, processPendingId, e);
        }
    }

    /**
     * 通知进行账务处理结果回调
     *
     * @param accountProcessNo .
     * @param processResultId  .
     */
    private void notifyDoAccountProcessCallBack(final String accountProcessNo, final Long processResultId) {
        logger.info("accountProcessNo={} processResultId={}", accountProcessNo, processResultId);
        if (processResultId == null) {
            return;
        }

        try {
            MsgDto msgDto = new MsgDto();
            msgDto.setTopic(AccountMsgDest.TOPIC_ACCOUNT_TRANSIT_PROCESS);
            msgDto.setTags(AccountMsgDest.TAG_ACCOUNT_TRANSIT_CALLBACK);
            msgDto.setTrxNo(accountProcessNo);
            msgDto.setJsonParam(JsonUtil.toString(Collections.singletonMap("processResultId", processResultId)));
            boolean isSuccess = rmqSender.sendOne(msgDto);
            logger.info("accountProcessNo={} processResultId={} isSuccess={} 通知进行账务处理结果回调完毕", accountProcessNo, processResultId, isSuccess);
        } catch (Throwable e) {
            logger.error("accountProcessNo={} 通知账务处理结果回调时出现异常", accountProcessNo, e);
        }
    }
}
