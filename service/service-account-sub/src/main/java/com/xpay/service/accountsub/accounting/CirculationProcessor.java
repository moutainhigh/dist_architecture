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
 * Author: Cmf
 * Date: 2019.12.19
 * Time: 15:02
 * Description: 账户内资金流转
 */
@Component
public class CirculationProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(CreditProcessor.class);
    @Autowired
    private AccountSubProcessDetailDao accountSubProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("账户内资金流转 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getAmountType() == AccountSubAmountTypeEnum.UNSETTLE_CIRCULATE_USABLE_AMOUNT.getValue()) {
            //待清算转移到余额
            this.unSettleToUsable(account, requestDto, processDto);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 待清算转移到可用余额
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void unSettleToUsable(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("待清算转移到可用余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUnsettleAmount())) {
            logger.info("trxNo={} unsettleAmount={} amount={} 入账金额不能大于待清算金额", processDto.getTrxNo(), account.getUnsettleAmount().toString(), processDto.getAmount().toString());
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("入账金额不能大于待清算金额");
        }

        AccountSub accountOld = copyAccount(account);
        account.setUnsettleAmount(AmountUtil.sub(account.getUnsettleAmount(), processDto.getAmount()));
        account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), processDto.getAmount()));

        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }
}
