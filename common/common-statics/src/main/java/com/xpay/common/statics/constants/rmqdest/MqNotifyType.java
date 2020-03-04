package com.xpay.common.statics.constants.rmqdest;

import java.io.Serializable;

/**
 * Author: Cmf
 * Date: 2019.11.28
 * Time: 16:56
 * Description: 通知类型
 */
public class MqNotifyType<T> implements Serializable {
    private String topic;
    private String tag;
    private int eventType;
    private String desc;
    private Class<T> dtoClass;

    MqNotifyType(String topic, String tag, int eventType, String desc, Class<T> dtoClass) {
        this.topic = topic;
        this.tag = tag;
        this.eventType = eventType;
        this.desc = desc;
        this.dtoClass = dtoClass;
    }

    public String getTopic() {
        return topic;
    }

    public String getTag() {
        return tag;
    }

    public int getEventType() {
        return eventType;
    }

    public String getDesc() {
        return desc;
    }

    public Class<T> getDtoClass() {
        return dtoClass;
    }

}

