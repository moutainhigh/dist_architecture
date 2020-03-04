package com.xpay.common.statics.dto.merchant.notify;

/**
 * Author: Cmf
 * Date: 2019.12.17
 * Time: 17:03
 * Description:商户异步通知DTO，
 * 业务层将该DTO作为MsgDto的jsonParam发送到商户通知的消息队列，自动完成商户通知
 */
public class MerchantNotifyDto {
    private String callbackUrl;
    private Object data;
    private String mchNo;
    private String randStr;
    private String secKey = "";

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMchNo() {
        return mchNo;
    }

    public void setMchNo(String mchNo) {
        this.mchNo = mchNo;
    }

    public String getRandStr() {
        return randStr;
    }

    public void setRandStr(String randStr) {
        this.randStr = randStr;
    }

    public String getSecKey() {
        return secKey;
    }

    public void setSecKey(String secKey) {
        this.secKey = secKey;
    }
}
