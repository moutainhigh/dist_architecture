package com.xpay.facade.accounttransit.entity;

import com.xpay.common.statics.annotations.PK;
import com.xpay.common.util.utils.BeanUtil;

import java.io.Serializable;

/**
 * 在途账户账务处理明细表
 */
public class AccountTransitProcessDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    @PK
    private Long id;
    private Integer version;

    /**
     * 记账时间
     */
    private java.util.Date createTime;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 商户类型 {@link com.xpay.common.statics.enums.merchant.MerchantTypeEnum}
     */
    private Integer merchantType;

    /**
     * 在途余额
     */
    private java.math.BigDecimal transitAmount;

    /**
     * 变动金额(交易金额)
     */
    private java.math.BigDecimal alterAmount;

    /**
     * 在途余额变动金额
     */
    private java.math.BigDecimal alterTransitAmount;

    /**
     * 账务处理流水号
     */
    private String accountProcessNo;

    /**
     * 交易流水号
     */
    private String trxNo;

    /**
     * 商户订单号
     */
    private String mchTrxNo;

    /**
     * 订单交易时间
     */
    private java.util.Date trxTime;

    /**
     * 业务类型com.xpay.common.statics.enums.product.BusinessType
     */
    private Integer bizType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账务处理类型
     */
    private Integer processType;

    /**
     * 扣款确认状态：1：待确认，2：确认扣款，3：确认退回，4：无须确认【若不是扣款的账务明细，设置为4】
     * {@link com.xpay.common.statics.enums.account.AccountDebitCommitStageEnum}
     */
    private Integer debitCommitStage;

    /**
     * 附加信息
     */
    private String extraInfo;

    //columns END

    //region 非数据库字段
    /**
     * 数据是否在历史表
     */
    private boolean inHistory;
    //endregion


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantNo() {
        return this.merchantNo;
    }

    public Integer getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(Integer merchantType) {
        this.merchantType = merchantType;
    }

    public void setTransitAmount(java.math.BigDecimal transitAmount) {
        this.transitAmount = transitAmount;
    }

    public java.math.BigDecimal getTransitAmount() {
        return this.transitAmount;
    }

    public void setAlterAmount(java.math.BigDecimal alterAmount) {
        this.alterAmount = alterAmount;
    }

    public java.math.BigDecimal getAlterAmount() {
        return this.alterAmount;
    }

    public void setAlterTransitAmount(java.math.BigDecimal alterTransitAmount) {
        this.alterTransitAmount = alterTransitAmount;
    }

    public java.math.BigDecimal getAlterTransitAmount() {
        return this.alterTransitAmount;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public String getAccountProcessNo() {
        return this.accountProcessNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public String getTrxNo() {
        return this.trxNo;
    }

    public void setMchTrxNo(String mchTrxNo) {
        this.mchTrxNo = mchTrxNo;
    }

    public String getMchTrxNo() {
        return this.mchTrxNo;
    }

    public void setTrxTime(java.util.Date trxTime) {
        this.trxTime = trxTime;
    }

    public java.util.Date getTrxTime() {
        return this.trxTime;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getBizType() {
        return this.bizType;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public Integer getProcessType() {
        return this.processType;
    }

    public Integer getDebitCommitStage() {
        return debitCommitStage;
    }

    public void setDebitCommitStage(Integer debitCommitStage) {
        this.debitCommitStage = debitCommitStage;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getExtraInfo() {
        return this.extraInfo;
    }

    public boolean isInHistory() {
        return inHistory;
    }


    /**
     * 根据明细历史创建一个账务明细实体
     *
     * @param history 明细历史
     * @return .
     */
    public static AccountTransitProcessDetail newFromHistory(AccountTransitProcessDetailHistory history) {
        if (history == null) {
            return null;
        } else {
            AccountTransitProcessDetail detail = new AccountTransitProcessDetail();
            BeanUtil.copyProperties(history, detail);
            detail.inHistory = true; //赋值需要在copyProperties后面
            return detail;
        }
    }

}
