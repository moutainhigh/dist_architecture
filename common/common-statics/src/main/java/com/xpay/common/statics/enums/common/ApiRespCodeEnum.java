package com.xpay.common.statics.enums.common;

public enum ApiRespCodeEnum {
    SUCCESS("01", "受理成功"),
    MCH_FAIL("02", "商户信息错误"),
    SIGN_FAIL("03", "验签失败"),
    PARAM_FAIL("04", "验参失败"),
    BIZ_FAIL("05", "业务校验失败"),
    SYS_FORBID("06", "系统限制"),
    UNKNOWN("07", "受理未知"),
    ;

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private ApiRespCodeEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
