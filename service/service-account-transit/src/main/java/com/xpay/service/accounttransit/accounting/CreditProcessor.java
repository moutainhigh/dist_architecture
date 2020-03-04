package com.xpay.service.accounttransit.accounting;

import com.xpay.common.statics.dto.accounttransit.AccountTransitAmountTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import com.xpay.service.accounttransit.dao.AccountTransitProcessDetailDao;
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
    private AccountTransitProcessDetailDao accountTransitProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        //入账不需要校验账户状态，出款时才需要
        if (processDto.getAmountType() == AccountTransitAmountTypeEnum.TRANSIT_AMOUNT.getValue()) {
            //入账到可用余额
            this.transitCredit(account, requestDto, processDto);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 入账到在途余额
     */
    private void transitCredit(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("入账到在途余额 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        //待清算金额入账
        AccountTransit accountOld = copyAccount(account);
        account.setTransitAmount(AmountUtil.add(account.getTransitAmount(), processDto.getAmount()));
        AccountTransitProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountTransitProcessDetailDao.uniqueInsert(accountDetail);
    }
}
