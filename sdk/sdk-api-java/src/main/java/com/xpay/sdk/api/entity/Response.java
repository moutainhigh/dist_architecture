package com.xpay.sdk.api.entity;

import com.xpay.sdk.api.enums.RespCode;

/**
 * 响应给商户的VO
 */
public class Response {
    private String resp_code;
    private String resp_msg;
    private String data;
    private String mch_no;
    private String rand_str;
    private String sign;
    private String sign_type;
    private String sec_key;

    public String getResp_code() {
        return resp_code;
    }

    public void setResp_code(String resp_code) {
        this.resp_code = resp_code;
    }

    public String getResp_msg() {
        return resp_msg;
    }

    public void setResp_msg(String resp_msg) {
        this.resp_msg = resp_msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMch_no() {
        return mch_no;
    }

    public void setMch_no(String mch_no) {
        this.mch_no = mch_no;
    }

    public String getRand_str() {
        return rand_str;
    }

    public void setRand_str(String rand_str) {
        this.rand_str = rand_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSec_key() {
        return sec_key;
    }

    public void setSec_key(String sec_key) {
        this.sec_key = sec_key;
    }

    /**
     * 判断本次请求是否成功
     * @return
     */
    public boolean isSuccess(){
        return RespCode.SUCCESS.getCode().equals(resp_code);
    }

    /**
     * 获取resp_msg中的错误码
     * @return
     */
    public String getRespMsgCode(){
        int index = -1;
        if(resp_msg != null && (index = resp_msg.indexOf("[")) >= 0 && resp_msg.endsWith("]")){
            return resp_msg.substring(index+1, resp_msg.length()-1);
        }else{
            return null;
        }
    }
}
