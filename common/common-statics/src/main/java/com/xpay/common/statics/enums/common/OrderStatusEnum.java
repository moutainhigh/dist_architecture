package com.xpay.common.statics.enums.common;

public enum OrderStatusEnum {
    PENDING("01", "受理中"),
    PROCESSING("02", "处理中"),
    SUCCESS("03", "成功"),
    FAIL("04", "失败"),

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

    private OrderStatusEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
