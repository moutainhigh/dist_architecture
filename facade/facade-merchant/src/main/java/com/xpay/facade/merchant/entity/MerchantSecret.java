/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.merchant.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 商户密钥管理
 */
public class MerchantSecret implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    private java.util.Date createTime = new Date();

    /**
     * VERSION
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
     * 签名方式SignTypeEnum
     */
    private Integer signType;

    /**
     * 平台公钥
     */
    private String platformPublicKey;

    /**
     * 平台私钥
     */
    private String platformPrivateKey;

    /**
     * 商户公钥
     */
    private String merchantPublicKey;

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

    public void setSignType(Integer signType) {
        this.signType = signType;
    }

    public Integer getSignType() {
        return this.signType;
    }

    public void setPlatformPublicKey(String platformPublicKey) {
        this.platformPublicKey = platformPublicKey;
    }

    public String getPlatformPublicKey() {
        return this.platformPublicKey;
    }

    public void setPlatformPrivateKey(String platformPrivateKey) {
        this.platformPrivateKey = platformPrivateKey;
    }

    public String getPlatformPrivateKey() {
        return this.platformPrivateKey;
    }

    public void setMerchantPublicKey(String merchantPublicKey) {
        this.merchantPublicKey = merchantPublicKey;
    }

    public String getMerchantPublicKey() {
        return this.merchantPublicKey;
    }

}
