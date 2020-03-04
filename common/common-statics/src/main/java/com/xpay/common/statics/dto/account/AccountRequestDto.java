package com.xpay.common.statics.dto.account;

import java.io.Serializable;

public class AccountRequestDto implements Serializable {
    private static final long serialVersionUID = -1636746635745244115L;
    /**
     * 账务处理流水号
     */
    private String accountProcessNo;
    /**
     * 是否需要加急账务处理&加急回调
     */
    private Boolean urgent;
    /**
     * 是否来自异步处理(账务内部逻辑处理时使用)
     */
    private Boolean fromAsync;
    /**
     * 账务处理结果回调目的地(MQ队列名)，如果是RocketMQ的topic、tags，可以用英文的冒号分割，如：my-topic:tags_one
     */
    private String callbackQueue;
    /**
     * MQ消息回调额外信息
     */
    private String callbackExtraMsg;

    public String getAccountProcessNo() {
        return accountProcessNo;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Boolean getFromAsync() {
        return fromAsync;
    }

    public void setFromAsync(Boolean fromAsync) {
        this.fromAsync = fromAsync;
    }

    public String getCallbackQueue() {
        return callbackQueue;
    }

    public void setCallbackQueue(String callbackQueue) {
        this.callbackQueue = callbackQueue;
    }

    public String getCallbackExtraMsg() {
        return callbackExtraMsg;
    }

    public void setCallbackExtraMsg(String callbackExtraMsg) {
        this.callbackExtraMsg = callbackExtraMsg;
    }
}
