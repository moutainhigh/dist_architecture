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
public class CirculationProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(CreditProcessor.class);
    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("账户内资金流转 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getAmountType() == AccountMchAmountTypeEnum.UNSETTLE_CIRCULATE_USABLE_AMOUNT.getValue()) {
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
    private void unSettleToUsable(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("待清算转移到可用余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUnsettleAmount())) {
            logger.info("trxNo={} unsettleAmount={} amount={} 入账金额不能大于待清算金额", processDto.getTrxNo(), account.getUnsettleAmount().toString(), processDto.getAmount().toString());
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("入账金额不能大于待清算金额");
        }

        AccountMch accountOld = copyAccount(account);
        account.setUnsettleAmount(AmountUtil.sub(account.getUnsettleAmount(), processDto.getAmount()));
        account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), processDto.getAmount()));

        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
    }
}
