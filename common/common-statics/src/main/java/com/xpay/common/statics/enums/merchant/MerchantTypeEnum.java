package com.xpay.common.statics.enums.merchant;

import java.util.Arrays;

/**
 * 商户类型
 *
 * @author luobinzhao
 * @date 2020/1/14 16:56
 */
public enum MerchantTypeEnum {
    /**
     * 平台商户
     */
    PLAT_MERCHANT(1, "平台商户"),

    /**
     * 子商户
     */
    SUB_MERCHANT(2, "子商户"),
    ;

    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;

    MerchantTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MerchantTypeEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElse(null);
    }
}
