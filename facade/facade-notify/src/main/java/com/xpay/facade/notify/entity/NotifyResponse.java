/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.notify.entity;

import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Author: Cmf
 * Date: 2019.11.28
 * Time: 16:46
 * Description: 通知响应记录
 */
public class NotifyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * ID
     */
    private Long id;

    /**
     * VERSION
     */
    private Integer version;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 通知记录ID
     */
    private Long notifyId;

    /**
     * 响应方
     */
    private String rspSign;

    /**
     * 响应时间
     */
    private Date rspTime;

    /**
     * 响应状态,1:成功,-1:失败
     */
    private Integer rspStatus;

    /**
     * 响应信息
     */
    private String rspMsg;

    /**
     * 响应历史
     */
    private String rspHistories;

    /**
     * 附加信息
     */
    private Object extraInfo;

    //columns END


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

    public void setNotifyId(Long notifyId) {
        this.notifyId = notifyId;
    }

    public Long getNotifyId() {
        return this.notifyId;
    }

    public void setRspSign(String rspSign) {
        this.rspSign = rspSign;
    }

    public String getRspSign() {
        return this.rspSign;
    }

    public Date getRspTime() {
        return rspTime;
    }

    public void setRspTime(Date rspTime) {
        this.rspTime = rspTime;
    }

    public void setRspStatus(Integer rspStatus) {
        this.rspStatus = rspStatus;
    }

    public Integer getRspStatus() {
        return this.rspStatus;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    public String getRspMsg() {
        return this.rspMsg;
    }

    public String getRspHistories() {
        return rspHistories;
    }

    public List<RspHistory> getRspHistoriesAsList() {
        if (StringUtil.isEmpty(this.rspHistories)) {
            return new ArrayList<>();
        } else {
            return JsonUtil.toList(this.rspHistories, RspHistory.class);
        }
    }

    public void setRspHistories(String rspHistories) {
        this.rspHistories = rspHistories;
    }

    public void setExtraInfo(Object extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Object getExtraInfo() {
        return this.extraInfo;
    }


    /**
     * 响应历史
     */
    public static class RspHistory {
        private Date rspTime;
        private Integer rspStatus;
        private String rspMsg;

        public RspHistory() {
        }

        public Date getRspTime() {
            return rspTime;
        }

        public void setRspTime(Date rspTime) {
            this.rspTime = rspTime;
        }

        public Integer getRspStatus() {
            return rspStatus;
        }

        public void setRspStatus(Integer rspStatus) {
            this.rspStatus = rspStatus;
        }

        public String getRspMsg() {
            return rspMsg;
        }

        public void setRspMsg(String rspMsg) {
            this.rspMsg = rspMsg;
        }

        public RspHistory(Date rspTime, Integer rspStatus, String rspMsg) {
            this.rspTime = rspTime;
            this.rspStatus = rspStatus;
            this.rspMsg = rspMsg;
        }
    }

}
