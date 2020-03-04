/*
 * Powered By [cyf.com]
 */
package com.xpay.facade.lock.entity;


import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;
import java.util.Date;

/**
 * 并发控制锁实体类
 *
 * @author chenyf
 */
public class GlobalLock implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    @PK
    private java.lang.Long id;
    private java.util.Date createTime = new Date();
    private java.lang.Long version;
    /**
     * 资源id
     */
    private String resourceId;
    /**
     * 资源状态(1=空闲 2=锁定)
     */
    private Integer resourceStatus;
    /**
     * 客户端id(锁持有者)
     */
    private String clientId;

    /**
     * 客户端标识
     */
    private String clientFlag;

    /**
     * 上锁时间
     */
    private java.util.Date lockTime;

    /**
     * 过期时间(NULL表示永不过期)
     */
    private java.util.Date expireTime;
    //columns END


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(Integer resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientFlag() {
        return clientFlag;
    }

    public void setClientFlag(String clientFlag) {
        this.clientFlag = clientFlag;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}

