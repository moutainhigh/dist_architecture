/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.accountmch.entity;

import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;

/**
 * 主商户账务唯一约束表
 */
public class AccountMchCommonUnique implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * id
     */
    @PK
    private Long id;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 唯一约束键(定长32位)
     */
    private String uniqueKey;

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

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

}
