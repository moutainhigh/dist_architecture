package com.xpay.service.accountsub.accounting;

import com.xpay.common.statics.dto.accountsub.AccountSubAmountTypeEnum;
import com.xpay.common.statics.exception.AccountSubExceptions;
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
 * Author: Cmf
 * Date: 2019.12.19
 * Time: 14:32
 * Description: 账务出款逻辑
 */
@Component
public class DebitProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(DebitProcessor.class);

    @Autowired
    private AccountSubProcessDetailDao accountSubProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getAmountType() == AccountSubAmountTypeEnum.UNSETTLE_AMOUNT.getValue()) {
            //使用待清算金额扣款
            this.unsettleDebit(account, requestDto, processDto);
        } else if (processDto.getAmountType() == AccountSubAmountTypeEnum.USABLE_AMOUNT.getValue()) {
            //使用可用余额扣款
            this.usableDebit(account, requestDto, processDto);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 使用待清算金额扣款
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void unsettleDebit(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("使用待清算金额扣款 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUnsettleAmount())) {
            logger.error("processNo={} trxNo={} amount={} unsettleAmount={} 待清算金额不足", requestDto.getAccountProcessNo(), processDto.getTrxNo(), processDto.getAmount(), account.getUnsettleAmount());
            throw AccountSubExceptions.UNSETTLE_AMOUNT_NOT_ENOUGH.newWithErrMsg("待清算金额不足");
        }

        AccountSub accountOld = copyAccount(account);
        account.setUnsettleAmount(AmountUtil.sub(account.getUnsettleAmount(), processDto.getAmount()));
        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }


    /**
     * 使用可用余额扣款
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void usableDebit(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("使用可用余额扣款 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUsableAmount())) {
            logger.error("processNo={} trxNo={} amount={} usableAmount={} 可用余额不足", requestDto.getAccountProcessNo(), processDto.getTrxNo(), processDto.getAmount(), account.getUsableAmount());
            throw AccountSubExceptions.USABLE_AMOUNT_NOT_ENOUGH.newWithErrMsg("可用余额不足");
        }

        AccountSub accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.sub(account.getUsableAmount(), processDto.getAmount()));
        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }
}
