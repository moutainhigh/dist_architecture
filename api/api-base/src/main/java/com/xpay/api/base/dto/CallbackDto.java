package com.xpay.api.base.dto;

import com.xpay.api.base.annonation.NotSign;

/**
 * 异步回调的数据传输对象
 */
public class CallbackDto {
    private Object data;
    private String mch_no;
    private String rand_str;
    private String sign_type;
    @NotSign
    private String sign;
    @NotSign
    private String sec_key = "";

    public String getMch_no() {
        return mch_no;
    }

    public void setMch_no(String mch_no) {
        this.mch_no = mch_no;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getRand_str() {
        return rand_str;
    }

    public void setRand_str(String rand_str) {
        this.rand_str = rand_str;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSec_key() {
        return sec_key;
    }

    public void setSec_key(String sec_key) {
        this.sec_key = sec_key;
    }
}
