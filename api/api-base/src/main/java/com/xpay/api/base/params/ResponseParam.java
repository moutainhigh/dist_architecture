package com.xpay.api.base.params;

import com.xpay.api.base.annonation.NotSign;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.RandomUtil;

/**
 * 响应用户请求的参数
 * @author chenyf
 * @date 2018-12-15
 */
public class ResponseParam {
    private String resp_code;
    private String resp_msg;
    private String mch_no;
    private String data;
    private String rand_str;
    private String sign_type;
    @NotSign
    private String sign;
    @NotSign
    private String sec_key;

    public static ResponseParam success(String mchNo){
        ResponseParam responseParam = new ResponseParam();
        responseParam.setResp_code(ApiRespCodeEnum.SUCCESS.getCode());
        responseParam.setResp_msg(ApiRespCodeEnum.SUCCESS.getMsg());
        responseParam.setMch_no(mchNo);
        responseParam.setSign("");
        responseParam.setRand_str(RandomUtil.get32LenStr());
        return responseParam;
    }

    public static ResponseParam unknown(String mchNo){
        ResponseParam responseParam = new ResponseParam();
        responseParam.setResp_code(ApiRespCodeEnum.UNKNOWN.getCode());
        responseParam.setMch_no(mchNo);
        responseParam.setSign("");
        responseParam.setRand_str(RandomUtil.get32LenStr());
        return responseParam;
    }

    public static ResponseParam fail(String mchNo, String respCode, String respMsg){
        if(ApiRespCodeEnum.SUCCESS.getCode().equals(respCode)){
            throw new BizException("错误的响应码：" + respCode);
        }

        ResponseParam responseParam = new ResponseParam();
        responseParam.setResp_code(respCode);
        responseParam.setResp_msg(respMsg);
        responseParam.setMch_no(mchNo);
        responseParam.setSign("");
        responseParam.setRand_str(RandomUtil.get32LenStr());
        return responseParam;
    }

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

    public String getMch_no() {
        return mch_no;
    }

    public void setMch_no(String mch_no) {
        this.mch_no = mch_no;
    }

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
}
