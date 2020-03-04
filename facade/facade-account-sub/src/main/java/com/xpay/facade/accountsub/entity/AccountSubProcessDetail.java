package com.xpay.facade.accountsub.entity;

import com.xpay.common.statics.annotations.PK;
import com.xpay.common.util.utils.BeanUtil;

import java.io.Serializable;

/**
 * 子商户账务处理明细表
 */
public class AccountSubProcessDetail implements Serializable {

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
     * 父商户编号
     */
    private String parentMerchantNo;

    /**
     * 待清算余额
     */
    private java.math.BigDecimal unsettleAmount;

    /**
     * 可用余额
     */
    private java.math.BigDecimal usableAmount;

    /**
     * 变动金额(交易金额)
     */
    private java.math.BigDecimal alterAmount;

    /**
     * 待清算余额变动金额
     */
    private java.math.BigDecimal alterUnsettleAmount;

    /**
     * 可用余额金额
     */
    private java.math.BigDecimal alterUsableAmount;

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

    public void setParentMerchantNo(String parentMerchantNo) {
        this.parentMerchantNo = parentMerchantNo;
    }

    public String getParentMerchantNo() {
        return this.parentMerchantNo;
    }

    public void setUnsettleAmount(java.math.BigDecimal unsettleAmount) {
        this.unsettleAmount = unsettleAmount;
    }

    public java.math.BigDecimal getUnsettleAmount() {
        return this.unsettleAmount;
    }

    public void setUsableAmount(java.math.BigDecimal usableAmount) {
        this.usableAmount = usableAmount;
    }

    public java.math.BigDecimal getUsableAmount() {
        return this.usableAmount;
    }

    public void setAlterAmount(java.math.BigDecimal alterAmount) {
        this.alterAmount = alterAmount;
    }

    public java.math.BigDecimal getAlterAmount() {
        return this.alterAmount;
    }

    public void setAlterUnsettleAmount(java.math.BigDecimal alterUnsettleAmount) {
        this.alterUnsettleAmount = alterUnsettleAmount;
    }

    public java.math.BigDecimal getAlterUnsettleAmount() {
        return this.alterUnsettleAmount;
    }

    public void setAlterUsableAmount(java.math.BigDecimal alterUsableAmount) {
        this.alterUsableAmount = alterUsableAmount;
    }

    public java.math.BigDecimal getAlterUsableAmount() {
        return this.alterUsableAmount;
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
    public static AccountSubProcessDetail newFromHistory(AccountSubProcessDetailHistory history) {
        if (history == null) {
            return null;
        } else {
            AccountSubProcessDetail detail = new AccountSubProcessDetail();
            BeanUtil.copyProperties(history, detail);
            detail.inHistory = true; //赋值需要在copyProperties后面
            return detail;
        }
    }

}
