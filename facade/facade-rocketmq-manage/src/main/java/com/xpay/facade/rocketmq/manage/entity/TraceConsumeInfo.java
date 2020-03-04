package com.xpay.facade.rocketmq.manage.entity;

import java.io.Serializable;
import java.util.Date;

public class TraceConsumeInfo implements Serializable {
    private Date consumeTime;
    private String consumeGroupName;
    private boolean consumeStatus;
    private String msgId;
    private String consumeTraceId;

    public Date getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Date consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getConsumeGroupName() {
        return consumeGroupName;
    }

    public void setConsumeGroupName(String consumeGroupName) {
        this.consumeGroupName = consumeGroupName;
    }

    public boolean isConsumeStatus() {
        return consumeStatus;
    }

    public void setConsumeStatus(boolean consumeStatus) {
        this.consumeStatus = consumeStatus;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getConsumeTraceId() {
        return consumeTraceId;
    }

    public void setConsumeTraceId(String consumeTraceId) {
        this.consumeTraceId = consumeTraceId;
    }
}
