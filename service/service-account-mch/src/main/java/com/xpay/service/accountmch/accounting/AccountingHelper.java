package com.xpay.service.accountmch.accounting;

import com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.statics.exception.AccountMchExceptions;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountmch.dto.AccountMchProcessDto;
import com.xpay.facade.accountmch.dto.AccountMchRequestDto;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author luobinzhao
 * @date 2020/1/14 15:04
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

    @Autowired
    private CirculationProcessor circulationProcessor;

    /**
     * 适配账务处理器
     *
     * @param processNo  .
     * @param processDto .
     * @return
     */
    public AccountingProcessor adaptProcessor(String processNo, AccountMchProcessDto processDto) {
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
            case SELF_CIRCULATION:
                return circulationProcessor;
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
    public static AccountMchProcessDetail buildAccountProcessDetail(AccountMch accountOld, AccountMch accountNew,
                                                                    AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        AccountMchProcessDetail detail = new AccountMchProcessDetail();
        detail.setVersion(0);
        detail.setCreateTime(processDto.getAccountTime() == null ? new Date() : processDto.getAccountTime());
        detail.setMerchantNo(accountNew.getMerchantNo());
        detail.setMerchantType(accountNew.getMerchantType());

        detail.setUnsettleAmount(accountNew.getUnsettleAmount());
        detail.setUsableAmount(accountNew.getUsableAmount());
        detail.setAlterAmount(processDto.getAmount());
        detail.setAlterUnsettleAmount(AmountUtil.sub(accountNew.getUnsettleAmount(), accountOld.getUnsettleAmount()));
        detail.setAlterUsableAmount(AmountUtil.sub(accountNew.getUsableAmount(), accountOld.getUsableAmount()));

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
    public static void validateAccountAmountCalcResult(String accountProcessNo, AccountMch accountNew) {
        if (AmountUtil.lessThan(accountNew.getUnsettleAmount(), BigDecimal.ZERO)) {
            logger.error("accountProcessNo={} userNo={} unsettleAmount={} 待清算余额小于零", accountProcessNo, accountNew.getMerchantNo(), accountNew.getUnsettleAmount());
            throw AccountMchExceptions.ACCOUNT_AMOUNT_BIZ_CALC_ERROR.newWithErrMsg("待清算金额小于零");
        } else if (AmountUtil.lessThan(accountNew.getUsableAmount(), BigDecimal.ZERO)) {
            logger.error("accountProcessNo={} userNo={} usableAmount={} 可用余额小于零", accountProcessNo, accountNew.getMerchantNo(), accountNew.getUsableAmount());
            throw AccountMchExceptions.ACCOUNT_AMOUNT_BIZ_CALC_ERROR.newWithErrMsg("可用余额小于零");
        }
    }
}
