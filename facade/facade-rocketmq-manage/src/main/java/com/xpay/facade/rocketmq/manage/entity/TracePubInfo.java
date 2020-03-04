package com.xpay.facade.rocketmq.manage.entity;

import java.io.Serializable;
import java.util.Date;

public class TracePubInfo implements Serializable {
    private String msgId;
    private Date pubTime;
    private String pubGroupName;
    private String topic;
    private String tags;
    private String keys;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    public String getPubGroupName() {
        return pubGroupName;
    }

    public void setPubGroupName(String pubGroupName) {
        this.pubGroupName = pubGroupName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }
}
