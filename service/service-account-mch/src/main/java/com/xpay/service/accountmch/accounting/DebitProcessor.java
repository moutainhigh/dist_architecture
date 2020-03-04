package com.xpay.service.accountmch.accounting;

import com.xpay.common.statics.dto.accountmch.AccountMchAmountTypeEnum;
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
 * @date 2020/1/14 15:10
 */
@Component
public class DebitProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(DebitProcessor.class);

    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getAmountType() == AccountMchAmountTypeEnum.UNSETTLE_AMOUNT.getValue()) {
            //使用待清算金额扣款
            this.unsettleDebit(account, requestDto, processDto);
        } else if (processDto.getAmountType() == AccountMchAmountTypeEnum.USABLE_AMOUNT.getValue()) {
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
    private void unsettleDebit(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("使用待清算金额扣款 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUnsettleAmount())) {
            logger.error("processNo={} trxNo={} amount={} unsettleAmount={} 待清算金额不足", requestDto.getAccountProcessNo(), processDto.getTrxNo(), processDto.getAmount(), account.getUnsettleAmount());
            throw AccountMchExceptions.UNSETTLE_AMOUNT_NOT_ENOUGH.newWithErrMsg("待清算金额不足");
        }

        AccountMch accountOld = copyAccount(account);
        account.setUnsettleAmount(AmountUtil.sub(account.getUnsettleAmount(), processDto.getAmount()));
        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }


    /**
     * 使用可用余额扣款
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void usableDebit(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("使用可用余额扣款 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUsableAmount())) {
            logger.error("processNo={} trxNo={} amount={} usableAmount={} 可用余额不足", requestDto.getAccountProcessNo(), processDto.getTrxNo(), processDto.getAmount(), account.getUsableAmount());
            throw AccountMchExceptions.USABLE_AMOUNT_NOT_ENOUGH.newWithErrMsg("可用余额不足");
        }

        AccountMch accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.sub(account.getUsableAmount(), processDto.getAmount()));
        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }
}
