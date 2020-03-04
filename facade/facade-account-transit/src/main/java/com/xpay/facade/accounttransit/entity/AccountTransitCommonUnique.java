package com.xpay.facade.accounttransit.entity;

import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;
import java.util.Date;

public class AccountTransitCommonUnique implements Serializable {
    private static final long serialVersionUID = 43493635241245483L;

    //columns START
    @PK
    private Long id;//id主键
    private Date createTime = new Date();//创建时间
    private String uniqueKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
