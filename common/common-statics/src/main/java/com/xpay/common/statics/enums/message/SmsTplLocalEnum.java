package com.xpay.common.statics.enums.message;

/**
 * 本地模版
 */
public enum SmsTplLocalEnum {



    ;
    private String code;
    private String desc;

    private SmsTplLocalEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static SmsTplLocalEnum getEnum(String name){
        return SmsTplLocalEnum.valueOf(name);
    }
}
