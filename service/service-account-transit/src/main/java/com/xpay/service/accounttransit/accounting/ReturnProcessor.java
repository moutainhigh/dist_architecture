package com.xpay.service.accounttransit.accounting;

import com.xpay.common.statics.dto.accounttransit.AccountTransitAmountTypeEnum;
import com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDetailDto;
import com.xpay.facade.accounttransit.dto.AccountTransitProcessDto;
import com.xpay.facade.accounttransit.dto.AccountTransitRequestDto;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import com.xpay.service.accounttransit.dao.AccountTransitProcessDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * Author: Cmf
 * Date: 2019.12.19
 * Time: 14:46
 * Description: 账务退回逻辑
 */
@Component
public class ReturnProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(ReturnProcessor.class);

    @Autowired
    private AccountTransitProcessDetailDao accountTransitProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("账务退回process processNo={} userNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getAmountType() == AccountTransitAmountTypeEnum.SOURCE_DEBIT_AMOUNT.getValue()) {
            //原路解冻退回
            this.sourceReturn(account, requestDto, processDto);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 原路解冻退回
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void sourceReturn(AccountTransit account, AccountTransitRequestDto requestDto, AccountTransitProcessDto processDto) {
        logger.info("账务原路退回 processNo={} userNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        AccountTransitProcessDetailDto detailDto = processDto.getDebitDetailDto();
        if (detailDto == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("原出款记录不能为null");
        } else if (!AmountUtil.equal(processDto.getAmount(), detailDto.getAlterAmount())) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("退回金额不等于出款时的扣款金额");
        }
        AccountTransit accountOld = copyAccount(account);

        //处理各个金额字段的值
        if (!AmountUtil.equal(detailDto.getAlterTransitAmount(), BigDecimal.ZERO)) {
            account.setTransitAmount(AmountUtil.add(account.getTransitAmount(), detailDto.getAlterTransitAmount().abs()));
        }

        AccountTransitProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountTransitProcessDetailDao.uniqueInsert(accountDetail);
        int updateRows = accountTransitProcessDetailDao.updateDebitCommitStage(Collections.singletonList(new String[]{processDto.getMerchantNo(), processDto.getTrxNo()}),
                AccountDebitCommitStageEnum.UN_COMMITTED, AccountDebitCommitStageEnum.RETURN_COMMITTED);
        if (updateRows != 1) {
            logger.error("修改账务扣款明细的扣款确认状态失败,修改成功数与预期数量不一致,updateRows={},processDto={}", updateRows, JsonUtil.toString(processDto));
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("修改账务扣款明细的扣款确认状态失败");
        }
    }

}
