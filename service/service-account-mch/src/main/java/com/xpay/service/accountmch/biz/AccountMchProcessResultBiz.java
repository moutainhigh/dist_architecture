package com.xpay.service.accountmch.biz;

import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.dto.accountmch.AccountMchProcessResultDto;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessResultAuditStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessResultCallbackStageEnum;
import com.xpay.common.statics.enums.account.AccountResultAuditTypeEnum;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountmch.dto.AccountMchProcessDto;
import com.xpay.facade.accountmch.dto.AccountMchRequestDto;
import com.xpay.facade.accountmch.entity.AccountMchProcessResult;
import com.xpay.facade.accountmch.entity.AccountMchProcessResultHistory;
import com.xpay.service.accountmch.dao.AccountMchProcessResultDao;
import com.xpay.service.accountmch.dao.AccountMchProcessResultHistoryDao;
import com.xpay.starter.comp.component.RMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luobinzhao
 * @date 2020/1/14 14:31
 */
@Component
public class AccountMchProcessResultBiz {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RMQSender rmqSender;
    @Autowired
    private AccountMchProcessPendingBiz accountMchProcessPendingBiz;
    @Autowired
    private AccountMchProcessResultDao accountMchProcessResultDao;
    @Autowired
    private AccountMchProcessResultHistoryDao accountMchProcessResultHistoryDao;

    public void add(AccountMchProcessResult accountMchProcessResult) {
        accountMchProcessResultDao.insert(accountMchProcessResult);
    }

    public PageResult<List<AccountMchProcessResult>> listByPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountMchProcessResultDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountMchProcessResultHistoryDao.listPage(paramMap, pageParam);
            List<AccountMchProcessResult> collect = ((List<AccountMchProcessResultHistory>) pageResult.getData()).stream()
                    .map(AccountMchProcessResult::newFromHistory).collect(Collectors.toList());
            pageResult.setData(collect);
            return pageResult;
        }
    }

    public AccountMchProcessResult getAccountProcessResultById(Long id, boolean inHistory) {
        if (id == null) {
            return null;
        }
        if (!inHistory) {
            return accountMchProcessResultDao.getById(id);
        } else {
            return AccountMchProcessResult.newFromHistory(accountMchProcessResultHistoryDao.getById(id));
        }
    }

    /**
     * 账务处理结果审核
     *
     * @param id        .
     * @param auditType 账务处理结果审核类型 {@link AccountResultAuditTypeEnum}
     * @return .
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean auditProcessResult(Long id, Integer auditType) {
        logger.info("auditProcessResult id={},auditType={}", id, auditType);
        if (id == null || Arrays.stream(AccountResultAuditTypeEnum.values()).noneMatch(p -> p.getValue() == auditType)) {
            return false;
        }
        AccountMchProcessResult result = getAccountProcessResultById(id, false);
        if (result == null) {
            return false;
        } else if (result.getAuditStage() != AccountProcessResultAuditStageEnum.AUDIT_WAITING.getValue()) {
            return false;
        }
        result.setAuditStage(AccountProcessResultAuditStageEnum.AUDIT_FINISHED.getValue());
        if (auditType == AccountResultAuditTypeEnum.AUDIT_TO_SUCCESS.getValue()) {
            result.setProcessResult(PublicStatus.ACTIVE);
        } else if (auditType == AccountResultAuditTypeEnum.AUDIT_TO_FAIL.getValue()) {
            result.setProcessResult(PublicStatus.INACTIVE);
        } else if (auditType == AccountResultAuditTypeEnum.AUDIT_TO_REPROCESS.getValue()) {
            result.setProcessResult(PublicStatus.INACTIVE);
            result.setCallbackStage(AccountProcessResultCallbackStageEnum.NONE_SEND.getValue());
            accountMchProcessPendingBiz.updatePendingStatus(result.getAccountProcessNo(), AccountProcessPendingStageEnum.PENDING, AccountProcessPendingStageEnum.FINISHED);
        }
        try {
            accountMchProcessResultDao.update(result);
            return true;
        } catch (BizException ex) {
            logger.error("审核账务处理结果时失败,id={},auditType={}", id, auditType, ex);
            throw ex;
        }
    }

    /**
     * 发送处理结果的回调通知，然后更新为“已发送”
     *
     * @param id            .
     * @param isOnlySendMsg .
     * @param inHistory     .
     * @return .
     */
    public boolean sendProcessResultCallbackMsg(Long id, boolean isOnlySendMsg, boolean inHistory) {
        logger.info("==>id={} isOnlySendMsg={} inHistory={}", id, isOnlySendMsg, inHistory);
        if (id == null) {
            return false;
        }

        AccountMchProcessResult result = getAccountProcessResultById(id, inHistory);
        if (inHistory) {
            isOnlySendMsg = true;
        }

        if (result == null) {
            return false;
        } else if (result.getCallbackStage() == AccountProcessResultCallbackStageEnum.NONE_SEND.getValue()) {
            //处于"不发送"状态的不能发送
            return false;
        } else if (result.getAuditStage() == AccountProcessResultAuditStageEnum.AUDIT_WAITING.getValue()) {
            //处于"待审核"状态的不能发送
            return false;
        }

        //封装回调参数
        final AccountMchRequestDto requestDto = JsonUtil.toBean(result.getRequestDto(), AccountMchRequestDto.class);
        final AccountMchProcessResultDto resultDto = new AccountMchProcessResultDto();
        this.fillProcessResultVo(resultDto, result, requestDto);

        //把当前记录的发送阶段更新为"已发送"
        boolean successUpdate = false;
        if (isOnlySendMsg) {
            successUpdate = true;
        } else {
            successUpdate = accountMchProcessResultDao.updateAccountProcessResultToSentById(id);
        }

        if (successUpdate) {
            if (StringUtil.isEmpty(requestDto.getCallbackDestination())) {
                return true;
            }
            MsgDto msgDto = new MsgDto();
            if (requestDto.getCallbackDestination().contains(":")) {
                String[] arr = requestDto.getCallbackDestination().split(":");
                msgDto.setTopic(arr[0]);
                msgDto.setTags(arr[1]);
            } else {
                msgDto.setTopic(requestDto.getCallbackDestination());
            }
            msgDto.setTrxNo(requestDto.getAccountProcessNo());
            msgDto.setJsonParam(JsonUtil.toString(resultDto));
            boolean isSuccess = rmqSender.sendOne(msgDto);
            if (isSuccess) {
                logger.info("id={} accountProcessNo={} 账务处理结果回调成功", result.getId(), result.getAccountProcessNo());
            } else {
                throw CommonExceptions.UNEXPECT_ERROR.newWithErrMsg("发送账务处理结果回调失败");
            }
            return true;
        } else {
            return false;
        }
    }


    private void fillProcessResultVo(AccountMchProcessResultDto resultDto, AccountMchProcessResult result, AccountMchRequestDto requestDto) {
        resultDto.setAccountProcessNo(result.getAccountProcessNo());
        resultDto.setProcessResult(result.getProcessResult());
        resultDto.setErrorCode(result.getErrorCode());
        resultDto.setErrorMsg(result.getRemark());

        List<String> trxNos = new ArrayList<>();
        List<String> userNos = new ArrayList<>();
        List<Integer> processTypes = new ArrayList<>();
        List<String> accountTimes = new ArrayList<>();
        List<AccountMchProcessDto> processVoList = JsonUtil.toList(result.getProcessDtoList(), AccountMchProcessDto.class);
        for (AccountMchProcessDto processDto : processVoList) {
            trxNos.add(processDto.getTrxNo());
            userNos.add(processDto.getMerchantNo());
            processTypes.add(processDto.getProcessType());
            accountTimes.add(processDto.getAccountTime() == null ? null : DateUtil.formatDateTime(processDto.getAccountTime()));
        }
        resultDto.setUserNos(userNos);
        resultDto.setTrxNos(trxNos);
        resultDto.setProcessTypes(processTypes);
        resultDto.setAccountTimes(accountTimes);
    }

    public List<Long> listNeedCallBackResultId(Date createTimeBegin, Long minId, int number) {
        if (createTimeBegin == null || number == 0) {
            return Collections.emptyList();
        }
        return accountMchProcessResultDao.listNeedCallBackResultId(createTimeBegin, minId, number);
    }
}
