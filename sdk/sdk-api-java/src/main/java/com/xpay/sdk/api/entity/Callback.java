package com.xpay.sdk.api.entity;

public class Callback {
    private String data;
    private String rand_str;
    private String sign_type;
    private String mch_no;
    private String sign;
    private String sec_key;

    public String getData() {
        return data;
    }

    public void setData(String data) {
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

    public String getMch_no() {
        return mch_no;
    }

    public void setMch_no(String mch_no) {
        this.mch_no = mch_no;
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
