package com.xpay.facade.accounttransit.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountTransitProcessDetailDto implements Serializable {
    private static final long serialVersionUID = -1636546569587411315L;

    private String merchantNo;
    private String trxNo;
    private Integer processType;
    /**
     * 变动金额
     */
    private BigDecimal alterAmount;
    /**
     * 在途余额变动金额
     */
    private BigDecimal alterTransitAmount;

    /**
     * 扣款确认状态：1：待确认，2：确认扣款，3：确认退回，4：无须确认【若不是扣款的账务明细，设置为4】
     * {@link com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum}
     */
    private Integer debitCommitStage;


    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public BigDecimal getAlterAmount() {
        return alterAmount;
    }

    public void setAlterAmount(BigDecimal alterAmount) {
        this.alterAmount = alterAmount;
    }

    public BigDecimal getAlterTransitAmount() {
        return alterTransitAmount;
    }

    public void setAlterTransitAmount(BigDecimal alterTransitAmount) {
        this.alterTransitAmount = alterTransitAmount;
    }

    public Integer getDebitCommitStage() {
        return debitCommitStage;
    }

    public void setDebitCommitStage(Integer debitCommitStage) {
        this.debitCommitStage = debitCommitStage;
    }
}
