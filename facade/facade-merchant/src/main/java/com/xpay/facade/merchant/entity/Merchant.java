/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.merchant.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户表
 */
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private java.util.Date createTime = new Date();

    /**
     * 版本号
     */
    private Long version = 0L;

    /**
     * 修改时间
     */
    private java.util.Date modifyTime;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 父商户编号(如果是子商户时填写)
     */
    private String parentMerchantNo;

    /**
     * 商户状态com.xpay.common.statics.enums.merchant.MerchantStatusEnum
     */
    private Integer status;

    /**
     * 商户认证状态MerchantAuthStatusEnum
     */
    private Integer authStatus;

    /**
     * 商户类型:1:平台商户，2:子商户
     */
    private Integer merchantType;

    /**
     * 公司全称
     */
    private String fullName;

    /**
     * 公司简称
     */
    private String shortName;

    /**
     * 商户等级,{@link com.xpay.common.statics.enums.merchant.MerchantLevelEnum}
     */
    private Integer merchantLevel;

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

    public void setParentMerchantNo(String parentMerchantNo) {
        this.parentMerchantNo = parentMerchantNo;
    }

    public String getParentMerchantNo() {
        return this.parentMerchantNo;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }

    public Integer getAuthStatus() {
        return this.authStatus;
    }

    public void setMerchantType(Integer merchantType) {
        this.merchantType = merchantType;
    }

    public Integer getMerchantType() {
        return this.merchantType;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setMerchantLevel(Integer merchantLevel) {
        this.merchantLevel = merchantLevel;
    }

    public Integer getMerchantLevel() {
        return this.merchantLevel;
    }

}
