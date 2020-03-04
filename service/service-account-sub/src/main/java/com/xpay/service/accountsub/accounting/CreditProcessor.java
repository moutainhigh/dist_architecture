package com.xpay.service.accountsub.accounting;

import com.xpay.common.statics.dto.accountsub.AccountSubAmountTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.facade.accountsub.dto.AccountSubProcessDto;
import com.xpay.facade.accountsub.dto.AccountSubRequestDto;
import com.xpay.facade.accountsub.entity.AccountSub;
import com.xpay.facade.accountsub.entity.AccountSubProcessDetail;
import com.xpay.service.accountsub.dao.AccountSubProcessDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: chenyf
 * Date: 2019.12.19
 * Time: 14:17
 * Description: 账务入账逻辑
 */
@Component
public class CreditProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(CreditProcessor.class);
    @Autowired
    private AccountSubProcessDetailDao accountSubProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        //入账不需要校验账户状态，出款时才需要
        if (processDto.getAmountType() == AccountSubAmountTypeEnum.USABLE_AMOUNT.getValue()) {
            //入账到可用余额
            this.usableCredit(account, requestDto, processDto);
        } else if (processDto.getAmountType() == AccountSubAmountTypeEnum.UNSETTLE_AMOUNT.getValue()) {
            //入账到可结算金额(如：退汇)
            this.unsettleCredit(account, requestDto, processDto);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 入账到待清算金额
     */
    private void unsettleCredit(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("入账到待清算余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        //待清算金额入账
        AccountSub accountOld = copyAccount(account);
        account.setUnsettleAmount(AmountUtil.add(account.getUnsettleAmount(), processDto.getAmount()));
        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }

    /**
     * 入账到可用余额
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void usableCredit(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("入账到可用余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        //入账到可用余额
        AccountSub accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), processDto.getAmount()));
        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }
}
