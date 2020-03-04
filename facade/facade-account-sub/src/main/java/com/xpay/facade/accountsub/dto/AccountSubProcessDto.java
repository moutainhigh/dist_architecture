package com.xpay.facade.accountsub.dto;

import com.xpay.common.statics.dto.accountsub.AccountSubAmountTypeEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccountSubProcessDto implements Serializable {
    private static final long serialVersionUID = -1636546596635244115L;

    /**
     * 交易发生时间
     */
    private Date trxTime;

    /**
     * 交易流水号
     */
    private String trxNo;

    /**
     * 商户交易流水号,如果不存在，则传空字符串""
     */
    private String mchTrxNo;

    /**
     * 账务处理类型
     * {@link com.xpay.common.statics.enums.account.AccountProcessTypeEnum}
     */
    private Integer processType;

    /**
     * 需要处理的是的哪种账户金额
     * {@link AccountSubAmountTypeEnum}
     */
    private Integer amountType;

    /**
     * 用户编号
     */
    private String merchantNo;

    /**
     * 账务处理金额
     */
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 业务类型 com.xpay.common.statics.enums.product.BusinessType
     */
    private Integer bizType;
    /**
     * 描述
     */
    private String desc;


    /**------------------------ 特定场景使用的属性 START ------------------------*/
    /**
     * 记账时间(账务内部逻辑处理时使用)
     */
    private Date accountTime = null;
    /**
     * 账务扣款记录(账务内部逻辑处理时使用)
     */
    private AccountSubProcessDetailDto debitDetailDto = null;

    /**
     * ------------------------ 特定场景使用的属性 END ------------------------
     */

    public Date getTrxTime() {
        return trxTime;
    }

    public void setTrxTime(Date trxTime) {
        this.trxTime = trxTime;
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public String getMchTrxNo() {
        return mchTrxNo;
    }

    public void setMchTrxNo(String mchTrxNo) {
        this.mchTrxNo = mchTrxNo;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public Integer getAmountType() {
        return amountType;
    }

    public void setAmountType(Integer amountType) {
        this.amountType = amountType;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(Date accountTime) {
        this.accountTime = accountTime;
    }

    public AccountSubProcessDetailDto getDebitDetailDto() {
        return debitDetailDto;
    }

    public void setDebitDetailDto(AccountSubProcessDetailDto debitDetailDto) {
        this.debitDetailDto = debitDetailDto;
    }
}
