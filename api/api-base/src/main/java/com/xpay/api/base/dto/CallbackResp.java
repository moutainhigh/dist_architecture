package com.xpay.api.base.dto;

/**
 * 异步回调时商户侧的响应对象
 */
public class CallbackResp {
    public final static String SUCCESS = "00";//成功
    public final static String RETRY = "01";//重试，当http调用失败时，需要重试
    public final static String FAIL = "02";//失败,如返回码正常，但数据有误

    /**
     * 商户响应的状态：00=成功 01=重试
     */
    private String status;
    /**
     * 商户响应的信息
     */
    private String message;
    /**
     * 网络异常等异常描述
     */
    private String errMsg;
    /**
     * 响应数据的签名，目前仅做预留
     */
    private String sign;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
