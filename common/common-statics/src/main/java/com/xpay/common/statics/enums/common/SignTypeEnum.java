package com.xpay.common.statics.enums.common;

import java.util.Arrays;

/**
 * 签名算法类型
 */
public enum SignTypeEnum {
    MD5(1, "MD5"),
    RSA(2, "RSA"),
    RSA2(3, "RSA2"),

    ;

    /**
     * 枚举值
     */
    private int value;
    /**
     * 描述
     */
    private String msg;

    public int getValue() {
        return value;
    }


    public String getMsg() {
        return msg;
    }

    SignTypeEnum(int value, String desc) {
        this.value = value;
        this.msg = desc;
    }

    public static SignTypeEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElse(null);
    }
}
