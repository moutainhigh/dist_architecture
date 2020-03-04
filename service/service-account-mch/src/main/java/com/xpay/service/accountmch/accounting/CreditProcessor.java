package com.xpay.service.accountmch.accounting;

import com.xpay.common.statics.dto.accountmch.AccountMchAmountTypeEnum;
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
 * @date 2020/1/14 15:10
 */
@Component
public class CreditProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(CreditProcessor.class);
    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        //入账不需要校验账户状态，出款时才需要
        if (processDto.getAmountType() == AccountMchAmountTypeEnum.USABLE_AMOUNT.getValue()) {
            //入账到可用余额
            this.usableCredit(account, requestDto, processDto);
        } else if (processDto.getAmountType() == AccountMchAmountTypeEnum.UNSETTLE_AMOUNT.getValue()) {
            //入账到可结算金额(如：退汇)
            this.unsettleCredit(account, requestDto, processDto);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 入账到待清算金额
     */
    private void unsettleCredit(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("入账到待清算余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        //待清算金额入账
        AccountMch accountOld = copyAccount(account);
        account.setUnsettleAmount(AmountUtil.add(account.getUnsettleAmount(), processDto.getAmount()));
        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }

    /**
     * 入账到可用余额
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void usableCredit(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("入账到可用余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        //入账到可用余额
        AccountMch accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), processDto.getAmount()));
        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }
}
