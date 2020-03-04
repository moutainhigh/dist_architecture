package com.xpay.service.accounttransit.accounting;

import com.xpay.common.statics.dto.accounttransit.AccountTransitAmountTypeEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.statics.exception.AccountTransitExceptions;
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
 * Time: 15:25
 * Description: 调账：调整账户参数或者进行差错处理
 */
@Component
public class AdjustProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(AdjustProcessor.class);
    @Autowired
    private AccountTransitProcessDetailDao accountTransitProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("账务调账 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getProcessType() == AccountProcessTypeEnum.ADJUST_ADD.getValue()) {
            if (processDto.getAmountType() == AccountTransitAmountTypeEnum.TRANSIT_AMOUNT.getValue()) {
                this.adjustTransitAdd(account, requestDto, processDto);
            } else {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
            }
        } else if (processDto.getProcessType() == AccountProcessTypeEnum.ADJUST_SUB.getValue()) {
            if (processDto.getAmountType() == AccountTransitAmountTypeEnum.TRANSIT_AMOUNT.getValue()) {
                this.adjustTransitSub(account, requestDto, processDto);
            } else {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
            }
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的processType:" + processDto.getProcessType());
        }
    }

    /**
     * 差错处理 - 在途余额调增
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void adjustTransitAdd(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("在途余额调增 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        AccountTransit accountOld = copyAccount(account);
        account.setTransitAmount(AmountUtil.add(account.getTransitAmount(), processDto.getAmount()));

        AccountTransitProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountTransitProcessDetailDao.uniqueInsert(accountDetail);
    }

    /**
     * 差错处理 - 在途余额调减
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void adjustTransitSub(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("在途余额调减 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getTransitAmount())) {
            logger.error("processNo={} trxNo={} amount={} transitAmount={} 在途余额不足", requestDto.getAccountProcessNo(),
                    processDto.getTrxNo(), processDto.getAmount(), account.getTransitAmount());
            throw AccountTransitExceptions.TRANSIT_AMOUNT_NOT_ENOUGH.newWithErrMsg("在途余额不足");
        }

        AccountTransit accountOld = copyAccount(account);
        account.setTransitAmount(AmountUtil.sub(account.getTransitAmount(), processDto.getAmount()));

        AccountTransitProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountTransitProcessDetailDao.uniqueInsert(accountDetail);
    }
}
