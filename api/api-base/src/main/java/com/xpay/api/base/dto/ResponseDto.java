package com.xpay.api.base.dto;

import com.alibaba.fastjson.JSON;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.RandomUtil;

/**
 * 响应给商户的VO，主要用作Controller的出参
 * @author chenyf
 * @date 2018-12-15
 */
public class ResponseDto<T> {
    private String respCode;
    private String respMsg;
    private String mchNo;
    private T data;
    private String randStr;
    private String sign;
    private String signType;
    private String secKey = "";

    public static <V> ResponseDto<V> success(String mchNo, String signType, V data) {
        return success(mchNo, signType, ApiRespCodeEnum.SUCCESS.getMsg(), data);
    }

    public static <V> ResponseDto<V> success(String mchNo, String signType, String respMsg, V data) {
        ResponseDto<V> vo = new ResponseDto();
        vo.setRespCode(ApiRespCodeEnum.SUCCESS.getCode());
        vo.setRespMsg(respMsg);
        vo.setMchNo(mchNo);
        vo.setData(data);
        vo.setRandStr(RandomUtil.get32LenStr());
        vo.setSignType(signType);
        return vo;
    }

    public static ResponseDto fail(String mchNo, String signType, String respCode, String respMsg){
        ResponseDto vo = new ResponseDto();
        vo.setRespCode(respCode);
        vo.setRespMsg(respMsg);
        vo.setMchNo(mchNo);
        vo.setRandStr(RandomUtil.get32LenStr());
        vo.setSignType(signType);
        return vo;
    }

    public static ResponseDto unknown(String mchNo, String signType){
        return unknown(mchNo, signType, ApiRespCodeEnum.UNKNOWN.getMsg());
    }

    public static ResponseDto unknown(String mchNo, String signType, String respMsg){
        ResponseDto vo = new ResponseDto();
        vo.setRespCode(ApiRespCodeEnum.UNKNOWN.getCode());
        vo.setRespMsg(respMsg);
        vo.setMchNo(mchNo);
        vo.setRandStr(RandomUtil.get32LenStr());
        vo.setSignType(signType);
        return vo;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public String getMchNo() {
        return mchNo;
    }

    public void setMchNo(String mchNo) {
        this.mchNo = mchNo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRandStr() {
        return randStr;
    }

    public void setRandStr(String randStr) {
        this.randStr = randStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSecKey() {
        return secKey;
    }

    public void setSecKey(String secKey) {
        this.secKey = secKey;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
