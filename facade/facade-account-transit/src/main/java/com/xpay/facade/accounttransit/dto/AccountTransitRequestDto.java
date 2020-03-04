package com.xpay.facade.accounttransit.dto;

import java.io.Serializable;

public class AccountTransitRequestDto implements Serializable {
    private static final long serialVersionUID = -1636746635745244115L;
    /**
     * 账务处理流水号
     */
    private String accountProcessNo;
    /**
     * 是否需要加急账务处理&加急回调
     */
    private boolean urgent;
    /**
     * 账务处理结果回调目的地(MQ队列)，如果是RocketMQ的topic、tags 是可以用英文的冒号分割，如：my-topic:tags_one
     */
    private String callbackDestination;
    /**
     * MQ消息类型
     */
    private Integer callbackEventType;
    /**
     * MQ消息回调额外信息
     */
    private String callbackExtraMsg;
    /**
     * 是否来自异步处理(账务内部逻辑处理时使用)
     */
    private boolean fromAsync;
    /**
     * 异步处理时的数据唯一约束是否需要amountType参与（请勿将默认值改为true）
     */
    private boolean dataUnqKeyWithAmountType;

    public String getAccountProcessNo() {
        return accountProcessNo;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public String getCallbackDestination() {
        return callbackDestination;
    }

    public void setCallbackDestination(String callbackDestination) {
        this.callbackDestination = callbackDestination;
    }

    public Integer getCallbackEventType() {
        return callbackEventType;
    }

    public void setCallbackEventType(Integer callbackEventType) {
        this.callbackEventType = callbackEventType;
    }

    public String getCallbackExtraMsg() {
        return callbackExtraMsg;
    }

    public void setCallbackExtraMsg(String callbackExtraMsg) {
        this.callbackExtraMsg = callbackExtraMsg;
    }

    public boolean getFromAsync() {
        return fromAsync;
    }

    public void setFromAsync(boolean fromAsync) {
        this.fromAsync = fromAsync;
    }

    public boolean getDataUnqKeyWithAmountType() {
        return dataUnqKeyWithAmountType;
    }

    public void setDataUnqKeyWithAmountType(boolean dataUnqKeyWithAmountType) {
        this.dataUnqKeyWithAmountType = dataUnqKeyWithAmountType;
    }
}
