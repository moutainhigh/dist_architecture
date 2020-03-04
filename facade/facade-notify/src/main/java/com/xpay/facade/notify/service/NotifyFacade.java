package com.xpay.facade.notify.service;


import com.xpay.common.statics.constants.rmqdest.MqNotifyType;

/**
 * 业务通知发送服务
 */
public interface NotifyFacade {
    /***
     * 发送业务通知，如果同一业务通知已存在，该方法会忽略已经发送的业务通知
     *
     * @param merchantNo 商户编号
     * @param mqNotifyType 通知类型
     * @param msgBody 通知信息DTO
     * @param trxNo 业务流水号
     * @param otherTrxNo subTrxNo,thirdTrxNo,可为空
     * @param <T> 通知信息DTO类型
     */
    <T> void sendNotify(String merchantNo, MqNotifyType<T> mqNotifyType, T msgBody, String trxNo, String... otherTrxNo);


    /**
     * 响应业务通知，如果同一个业务通知响应已存在，则该方法对更新其响应状态，否则创建业务通知响应
     *
     * @param notifyId  业务通知id
     * @param rspSign   响应方
     * @param rspStatus 响应状态 {@link com.xpay.common.statics.constants.common.PublicStatus}
     * @param rspMsg    响应信息
     */
    void response(long notifyId, String rspSign, int rspStatus, String rspMsg);


    /**
     * 重发业务通知，
     * 如果成功发送，则返回true
     * 如果业务通知不存在，或者发送失败，则返回false
     *
     * @param notifyId 业务通知id
     */
    boolean resend(long notifyId);


}
