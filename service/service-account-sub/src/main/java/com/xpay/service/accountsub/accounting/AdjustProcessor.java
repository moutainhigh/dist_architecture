package com.xpay.service.accountsub.accounting;

import com.xpay.common.statics.dto.accountsub.AccountSubAmountTypeEnum;
import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
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
 * Author: chenyf
 * Date: 2019.12.19
 * Time: 15:25
 * Description: 调账：调整账户参数或者进行差错处理
 */
@Component
public class AdjustProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(AdjustProcessor.class);
    @Autowired
    private AccountSubProcessDetailDao accountSubProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("账务调账 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getProcessType() == AccountProcessTypeEnum.ADJUST_ADD.getValue()) {
            if (processDto.getAmountType() == AccountSubAmountTypeEnum.USABLE_AMOUNT.getValue()) {
                this.adjustUsableAdd(account, requestDto, processDto);
            } else {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
            }
        } else if (processDto.getProcessType() == AccountProcessTypeEnum.ADJUST_SUB.getValue()) {
            if (processDto.getAmountType() == AccountSubAmountTypeEnum.USABLE_AMOUNT.getValue()) {
                this.adjustUsableSub(account, requestDto, processDto);
            } else {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
            }
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的processType:" + processDto.getProcessType());
        }
    }

    /**
     * 差错处理 - 可结算调增
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void adjustUsableAdd(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("可用余额调增 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        AccountSub accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), processDto.getAmount()));

        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }

    /**
     * 差错处理 - 可结算调减
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void adjustUsableSub(AccountSub account, AccountSubRequestDto requestDto, AccountSubProcessDto processDto) {
        logger.info("可用余额调减 processNo={} merchantNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());
        if (AmountUtil.greater(processDto.getAmount(), account.getUsableAmount())) {
            logger.error("processNo={} trxNo={} amount={} settledAmount={} 可用余额不足", requestDto.getAccountProcessNo(),
                    processDto.getTrxNo(), processDto.getAmount(), account.getUsableAmount());
            throw AccountSubExceptions.USABLE_AMOUNT_NOT_ENOUGH.newWithErrMsg("已结算金额不足");
        }

        AccountSub accountOld = copyAccount(account);
        account.setUsableAmount(AmountUtil.sub(account.getUsableAmount(), processDto.getAmount()));

        AccountSubProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountSubProcessDetailDao.uniqueInsert(accountDetail);
    }
}
