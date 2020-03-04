package com.xpay.service.accountmch.accounting;

import com.xpay.common.statics.dto.accountmch.AccountMchAmountTypeEnum;
import com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.accountmch.dto.AccountMchProcessDetailDto;
import com.xpay.facade.accountmch.dto.AccountMchProcessDto;
import com.xpay.facade.accountmch.dto.AccountMchRequestDto;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;
import com.xpay.service.accountmch.dao.AccountMchProcessDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * @author luobinzhao
 * @date 2020/1/14 15:10
 */
@Component
public class ReturnProcessor extends AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(ReturnProcessor.class);

    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;

    /**
     * 账务处理的具体逻辑
     */
    @Override
    protected void doProcess(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("账务退回process processNo={} userNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        if (processDto.getAmountType() == AccountMchAmountTypeEnum.SOURCE_DEBIT_AMOUNT.getValue()) {
            //原路解冻退回
            this.sourceReturn(account, requestDto, processDto);
        } else {
            CommonExceptions.BIZ_INVALID.newWithErrMsg("未预期的amountType:" + processDto.getAmountType());
        }
    }

    /**
     * 原路解冻退回
     *
     * @param account    .
     * @param requestDto .
     * @param processDto .
     */
    private void sourceReturn(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        logger.info("账务原路退回 processNo={} userNo={} trxNo={}", requestDto.getAccountProcessNo(), account.getMerchantNo(), processDto.getTrxNo());

        AccountMchProcessDetailDto detailDto = processDto.getDebitDetailDto();
        if (detailDto == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("原出款记录不能为null");
        } else if (!AmountUtil.equal(processDto.getAmount(), detailDto.getAlterAmount())) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("退回金额不等于出款时的扣款金额");
        }
        AccountMch accountOld = copyAccount(account);

        //处理各个金额字段的值
        if (!AmountUtil.equal(detailDto.getAlterUnsettleAmount(), BigDecimal.ZERO)) {
            account.setUnsettleAmount(AmountUtil.add(account.getUnsettleAmount(), detailDto.getAlterUnsettleAmount().abs()));
        }
        if (!AmountUtil.equal(detailDto.getAlterUsableAmount(), BigDecimal.ZERO)) {
            account.setUsableAmount(AmountUtil.add(account.getUsableAmount(), detailDto.getAlterUsableAmount().abs()));
        }

        AccountMchProcessDetail accountDetail = AccountingHelper.buildAccountProcessDetail(accountOld, account, requestDto, processDto);
        accountMchProcessDetailDao.uniqueInsert(accountDetail);
        int updateRows = accountMchProcessDetailDao.updateDebitCommitStage(Collections.singletonList(new String[]{processDto.getMerchantNo(), processDto.getTrxNo()}),
                AccountDebitCommitStageEnum.UN_COMMITTED, AccountDebitCommitStageEnum.RETURN_COMMITTED);
        if (updateRows != 1) {
            logger.error("修改账务扣款明细的扣款确认状态失败,修改成功数与预期数量不一致,updateRows={},processDto={}", updateRows, JsonUtil.toString(processDto));
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("修改账务扣款明细的扣款确认状态失败");
        }
    }
}
