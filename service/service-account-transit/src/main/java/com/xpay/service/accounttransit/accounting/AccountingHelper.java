package com.xpay.service.accounttransit.accounting;

import com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.statics.exception.AccountTransitExceptions;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: chenyf
 * Date: 2019.12.19
 * Time: 11:24
 * Description: 账务处理帮助类
 */
@Component
public class AccountingHelper {
    private static Logger logger = LoggerFactory.getLogger(AccountingHelper.class);

    @Autowired
    private CreditProcessor creditProcessor;

    @Autowired
    private DebitProcessor debitProcessor;

    @Autowired
    private ReturnProcessor returnProcessor;

    @Autowired
    private AdjustProcessor adjustProcessor;

    /**
     * 适配账务处理器
     *
     * @param processNo  .
     * @param processDto .
     * @return
     */
    public AccountingProcessor adaptProcessor(String processNo, AccountTransitProcessDto processDto) {
        logger.info("适配账务处理类 accountProcessNo={} userNo={} trxNo={}", processNo, processDto.getMerchantNo(), processDto.getTrxNo());

        AccountProcessTypeEnum typeEnum = AccountProcessTypeEnum.getEnum(processDto.getProcessType());
        if (typeEnum == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("无法适配，未预期的processType: " + processDto.getProcessType());
        }
        switch (typeEnum) {
            case CREDIT:
                return creditProcessor;
            case DEBIT:
                return debitProcessor;
            case RETURN:
                return returnProcessor;
            case ADJUST_ADD:
            case ADJUST_SUB:
                return adjustProcessor;
            default:
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未支持的processType: " + typeEnum.name());
        }
    }


    /**
     * 创建账务明细对象
     *
     * @param accountOld .
     * @param accountNew .
     * @param requestDto .
     * @param processDto .
     * @return
     */
    public static AccountTransitProcessDetail buildAccountProcessDetail(AccountTransit accountOld, AccountTransit accountNew,
                                                                        AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        AccountTransitProcessDetail detail = new AccountTransitProcessDetail();
        detail.setVersion(0);
        detail.setCreateTime(processDto.getAccountTime() == null ? new Date() : processDto.getAccountTime());
        detail.setMerchantNo(accountNew.getMerchantNo());
        detail.setMerchantType(accountNew.getMerchantType());

        detail.setTransitAmount(accountNew.getTransitAmount());
        detail.setAlterAmount(processDto.getAmount());
        detail.setAlterTransitAmount(AmountUtil.sub(accountNew.getTransitAmount(), accountOld.getTransitAmount()));

        detail.setAccountProcessNo(requestDto.getAccountProcessNo());
        detail.setTrxNo(processDto.getTrxNo());
        detail.setMchTrxNo(processDto.getMchTrxNo());
        detail.setTrxTime(processDto.getTrxTime());
        detail.setBizType(processDto.getBizType());
        detail.setRemark(StringUtil.subLeft(processDto.getDesc(), 100));
        detail.setProcessType(processDto.getProcessType());
        if (processDto.getProcessType() == AccountProcessTypeEnum.DEBIT.getValue()) {
            detail.setDebitCommitStage(AccountDebitCommitStageEnum.UN_COMMITTED.getValue());
        } else {
            detail.setDebitCommitStage(AccountDebitCommitStageEnum.NO_NEED_COMMIT.getValue());
        }
        detail.setExtraInfo(null);
        return detail;
    }

    /**
     * 校验账务处理完毕之后各金额字段是否异常（各金额不能小于0的已通过设置数据库的字段为unsigned来处理）
     *
     * @param accountProcessNo 账务处理流水号
     * @param accountNew       .
     */
    public static void validateAccountAmountCalcResult(String accountProcessNo, AccountTransit accountNew) {
        if (AmountUtil.lessThan(accountNew.getTransitAmount(), BigDecimal.ZERO)) {
            logger.error("accountProcessNo={} userNo={} transitAmount={} 在途余额小于零", accountProcessNo, accountNew.getMerchantNo(), accountNew.getTransitAmount());
            throw AccountTransitExceptions.ACCOUNT_AMOUNT_BIZ_CALC_ERROR.newWithErrMsg("在途余额小于零");
        }
    }
}
