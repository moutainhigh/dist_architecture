package com.xpay.service.accountmch.accounting;

import com.xpay.common.statics.dto.accountmch.AccountMchAmountTypeEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.statics.exception.AccountMchExceptions;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.facade.accountmch.dto.AccountMchProcessDto;
import com.xpay.facade.accountmch.dto.AccountMchRequestDto;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;
import com.xpay.service.accountmch.dao.AccountMchProcessDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luobinzhao
 * @date 2020/1/14 15:05
 */
@Component
public class AdjustProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(AdjustProcessor.class);
    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("账务调账 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getProcessType() == AccountProcessTypeEnum.ADJUST_ADD.getValue()) {
            if (processDto.getAmountType() == AccountMchAmountTypeEnum.USABLE_AMOUNT.getValue()) {
                this.adjustUsableAdd(account, requestDto, processDto);
            } else {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
            }
        } else if (processDto.getProcessType() == AccountProcessTypeEnum.ADJUST_SUB.getValue()) {
            if (processDto.getAmountType() == AccountMchAmountTypeEnum.USABLE_AMOUNT.getValue()) {
                this.adjustUsableSub(account, requestDto, processDto);
            } else {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
            }
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的processType:" + processDto.getProcessType());
        }
    }

    /**
     * 差错处理 - 已结算调增
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void adjustUsableAdd(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("可用余额调增 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        AccountMch accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), processDto.getAmount()));

        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }

    /**
     * 差错处理 - 已结算调减
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void adjustUsableSub(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("可用余额调减 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUsableAmount())) {
            logger.error("processNo={} trxNo={} amount={} settledAmount={} 可用余额不足", requestDto.getAccountProcessNo(),
                    processDto.getTrxNo(), processDto.getAmount(), account.getUsableAmount());
            throw AccountMchExceptions.USABLE_AMOUNT_NOT_ENOUGH.newWithErrMsg("可用余额不足");
        }

        AccountMch accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.sub(account.getUsableAmount(), processDto.getAmount()));

        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }
}
