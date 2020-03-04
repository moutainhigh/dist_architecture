package com.xpay.facade.accounttransit.entity;

import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;

/**
 * 在途账户表
 */
public class AccountTransit implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    @PK
    private java.lang.Long id;
    private java.util.Date createTime;
    private java.lang.Long version;

    /**
     * 修改时间
     */
    private java.util.Date modifyTime;

    /**
     * 商户编号
     */
    private java.lang.String merchantNo;

    /**
     * 商户类型 {@link com.xpay.common.statics.enums.merchant.MerchantTypeEnum}
     */
    private java.lang.Integer merchantType;

    /**
     * 账户状态
     */
    private java.lang.Integer status;

    /**
     * 待结算金额
     */
    private java.math.BigDecimal transitAmount;


    //columns END


    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public java.lang.Long getId() {
        return this.id;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setVersion(java.lang.Long version) {
        this.version = version;
    }

    public java.lang.Long getVersion() {
        return this.version;
    }

    public void setModifyTime(java.util.Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public java.util.Date getModifyTime() {
        return this.modifyTime;
    }

    public void setMerchantNo(java.lang.String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public java.lang.String getMerchantNo() {
        return this.merchantNo;
    }

    public Integer getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(Integer merchantType) {
        this.merchantType = merchantType;
    }

    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }

    public java.lang.Integer getStatus() {
        return this.status;
    }

    public void setTransitAmount(java.math.BigDecimal transitAmount) {
        this.transitAmount = transitAmount;
    }

    public java.math.BigDecimal getTransitAmount() {
        return this.transitAmount;
    }

}
