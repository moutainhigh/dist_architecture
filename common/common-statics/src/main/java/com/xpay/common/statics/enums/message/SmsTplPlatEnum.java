package com.xpay.common.statics.enums.message;

/**
 * 运营商平台的短信模版
 */
public enum SmsTplPlatEnum {
    ALIYUN_REGISTER("SMS_178455526", "阿里云注册验证码模版"),
    ALIYUN_LOGIN("SMS_178465535", "阿里云登陆验证码模版"),
    ALIYUN_COMMON("", "阿里云通用短信模版"),

    ;
    private String code;
    private String desc;

    private SmsTplPlatEnum(String code, String desc){
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

    public static SmsTplPlatEnum getEnum(String name){
        return SmsTplPlatEnum.valueOf(name);
    }
}
