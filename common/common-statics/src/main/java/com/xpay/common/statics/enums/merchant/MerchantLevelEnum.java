package com.xpay.common.statics.enums.merchant;

import java.util.Arrays;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 14:48
 * Description: 商户状态枚举
 */
public enum MerchantLevelEnum {

    SD(1, "SD"),
    SE(2, "SE"),
    SF(3, "SF"),
    SV(4, "SV"),
    ;
    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;

    MerchantLevelEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return value;
    }

    public static MerchantLevelEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElse(null);
    }
}