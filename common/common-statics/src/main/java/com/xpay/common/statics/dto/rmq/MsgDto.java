package com.xpay.common.statics.dto.rmq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息传输对象
 */
public class MsgDto implements Serializable {
    /**
     * 主题
     */
    protected String topic;

    /**
     * tags
     */
    protected String tags;

    /**
     * 交易流水号/业务流水号
     */
    protected String trxNo;

    /**
     * json格式的参数，非必填
     */
    protected String jsonParam = null;

    /**
     * RocketMQ的消息头以及特殊的自定义消息头，不要使用它来传递业务数据
     */
    protected Map<String, String> header;

    /**
     * 发送失败时填入的失败内容(需要生产者处理发送失败的消息时可使用)
     */
    protected String sendFailMsg = null;

    public MsgDto(){}

    public MsgDto(String topic, String tags){
        this.topic = topic;
        this.tags = tags;
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

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public void addHeader(String key, String value){
        if(header == null){
            header = new HashMap<>();
        }
        header.put(key, value);
    }

    public String getSendFailMsg() {
        return sendFailMsg;
    }

    public void setSendFailMsg(String sendFailMsg) {
        this.sendFailMsg = sendFailMsg;
    }
}
