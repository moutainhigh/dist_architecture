/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.notify.entity;

import java.io.Serializable;

/**
 * 新通知表唯一索引
 */
public class NotifyUnique implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * ID
     */
    private Long id;

    /**
     * 唯一约束
     */
    private String uniqueKey;

    /**
     * 创建日期
     */
    private java.util.Date createDate;

    //columns END


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    public java.util.Date getCreateDate() {
        return this.createDate;
    }

}
