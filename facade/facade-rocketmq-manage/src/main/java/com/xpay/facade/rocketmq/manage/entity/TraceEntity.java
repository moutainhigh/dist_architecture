package com.xpay.facade.rocketmq.manage.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.12.24
 * Time: 11:41
 * Description:
 */
public class TraceEntity implements Serializable {
    public String msgId;
    public String key;
    public TracePubInfo pub;
    public Map<String, List<TraceConsumeInfo>> groupConsumeInfos = new HashMap<>();

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TracePubInfo getPub() {
        return pub;
    }

    public void setPub(TracePubInfo pub) {
        this.pub = pub;
    }

    public Map<String, List<TraceConsumeInfo>> getGroupConsumeInfos() {
        return groupConsumeInfos;
    }

    public void setGroupConsumeInfos(Map<String, List<TraceConsumeInfo>> groupConsumeInfos) {
        this.groupConsumeInfos = groupConsumeInfos;
    }
}
