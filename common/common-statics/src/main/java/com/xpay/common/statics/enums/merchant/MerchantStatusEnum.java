package com.xpay.common.statics.enums.merchant;

import java.util.Arrays;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 14:48
 * Description: 商户状态枚举
 */
public enum MerchantStatusEnum {

    INACTIVE(-1, "已冻结"),
    ACTIVE(1, "已激活"),
    CREATING(2, "创建中"),
    CANCELLED(3, "已注销"),
    ;
    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;

    MerchantStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return value;
    }

    public static MerchantStatusEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElse(null);
    }
}