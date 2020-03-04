/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.accountmch.entity;

import com.xpay.common.statics.annotations.PK;
import com.xpay.common.statics.enums.merchant.MerchantTypeEnum;

import java.io.Serializable;

/**
 * 主商户账户表
 */
public class AccountMch implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START

    /**
     * 自增主键
     */
    @PK
    private Long id;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 修改时间
     */
    private java.util.Date modifyTime;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 账户类型
     * {@link MerchantTypeEnum}
     */
    private Integer merchantType;

    /**
     * 账户状态
     */
    private Integer status;

    /**
     * 待结算金额
     */
    private java.math.BigDecimal unsettleAmount;

    /**
     * 可用余额
     */
    private java.math.BigDecimal usableAmount;

    //columns END

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setModifyTime(java.util.Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public java.util.Date getModifyTime() {
        return this.modifyTime;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantNo() {
        return this.merchantNo;
    }

    public void setMerchantType(Integer merchantType) {
        this.merchantType = merchantType;
    }

    public Integer getMerchantType() {
        return this.merchantType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
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

}
