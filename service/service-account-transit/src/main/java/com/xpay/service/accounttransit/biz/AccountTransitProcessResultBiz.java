package com.xpay.service.accounttransit.biz;

import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.dto.accounttransit.AccountTransitProcessResultDto;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessResultAuditStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessResultCallbackStageEnum;
import com.xpay.common.statics.enums.account.AccountResultAuditTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessResult;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessResultHistory;
import com.xpay.service.accounttransit.dao.AccountTransitProcessResultDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessResultHistoryDao;
import com.xpay.starter.comp.component.RMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: chenyf
 * Date: 2019.12.18
 * Time: 18:41
 * Description: 账务处理结果逻辑处理层
 */
@Component
public class AccountTransitProcessResultBiz {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RMQSender rmqSender;
    @Autowired
    private AccountTransitProcessPendingBiz accountTransitProcessPendingBiz;
    @Autowired
    private AccountTransitProcessResultDao accountTransitProcessResultDao;
    @Autowired
    private AccountTransitProcessResultHistoryDao accountTransitProcessResultHistoryDao;

    public void add(AccountTransitProcessResult accountTransitProcessResult) {
        accountTransitProcessResultDao.insert(accountTransitProcessResult);
    }

    public PageResult<List<AccountTransitProcessResult>> listByPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountTransitProcessResultDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountTransitProcessResultHistoryDao.listPage(paramMap, pageParam);
            List<AccountTransitProcessResult> collect = ((List<AccountTransitProcessResultHistory>) pageResult.getData()).stream()
                    .map(AccountTransitProcessResult::newFromHistory).collect(Collectors.toList());
            pageResult.setData(collect);
            return pageResult;
        }
    }

    public AccountTransitProcessResult getAccountProcessResultById(Long id, boolean inHistory) {
        if (id == null) {
            return null;
        }
        if (!inHistory) {
            return accountTransitProcessResultDao.getById(id);
        } else {
            return AccountTransitProcessResult.newFromHistory(accountTransitProcessResultHistoryDao.getById(id));
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
        AccountTransitProcessResult result = getAccountProcessResultById(id, false);
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
            accountTransitProcessPendingBiz.updatePendingStatus(result.getAccountProcessNo(), AccountProcessPendingStageEnum.PENDING, AccountProcessPendingStageEnum.FINISHED);
        }
        try {
            accountTransitProcessResultDao.update(result);
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

        AccountTransitProcessResult result = getAccountProcessResultById(id, inHistory);
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
        final AccountTransitRequestDto requestDto = JsonUtil.toBean(result.getRequestDto(), AccountTransitRequestDto.class);
        final AccountTransitProcessResultDto resultDto = new AccountTransitProcessResultDto();
        this.fillProcessResultVo(resultDto, result, requestDto);

        //把当前记录的发送阶段更新为"已发送"
        boolean successUpdate = false;
        if (isOnlySendMsg) {
            successUpdate = true;
        } else {
            successUpdate = accountTransitProcessResultDao.updateAccountProcessResultToSentById(id);
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

    /**
     * 获取待发送的账务处理结果
     *
     * @param modifyDate 日期
     * @param number     每批次获取的数量
     * @return
     */
    public List<Long> listPendingAccountProcessResultId(Date modifyDate, int number) {
        if (modifyDate == null || number == 0) {
            return null;
        }

        List<Integer> auditStageList = Arrays.asList(
                AccountProcessResultAuditStageEnum.AUDIT_NONE.getValue(),
                AccountProcessResultAuditStageEnum.AUDIT_FINISHED.getValue());

        Map<String, Object> paramMap = new HashMap<>(10);
        paramMap.put("sendMsgStage", AccountProcessResultCallbackStageEnum.PENDING.getValue());
        paramMap.put("auditStageList", auditStageList);
        paramMap.put("number", number);
        paramMap.put("modifyDate", modifyDate);
        return accountTransitProcessResultDao.listAccountProcessResultId(paramMap);
    }

    private void fillProcessResultVo(AccountTransitProcessResultDto resultDto, AccountTransitProcessResult result, AccountTransitRequestDto requestDto) {
        resultDto.setAccountProcessNo(result.getAccountProcessNo());
        resultDto.setProcessResult(result.getProcessResult());
        resultDto.setErrorCode(result.getErrorCode());
        resultDto.setErrorMsg(result.getRemark());

        List<String> trxNos = new ArrayList<>();
        List<String> userNos = new ArrayList<>();
        List<Integer> processTypes = new ArrayList<>();
        List<String> accountTimes = new ArrayList<>();
        List<AccountTransitProcessDto> processVoList = JsonUtil.toList(result.getProcessDtoList(), AccountTransitProcessDto.class);
        for (AccountTransitProcessDto processDto : processVoList) {
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
        return accountTransitProcessResultDao.listNeedCallBackResultId(createTimeBegin, minId, number);
    }
}
