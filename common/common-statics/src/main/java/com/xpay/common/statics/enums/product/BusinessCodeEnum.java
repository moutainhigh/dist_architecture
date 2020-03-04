package com.xpay.common.statics.enums.product;

public enum BusinessCodeEnum {
    RECEIVE_NET_B2C(1, "网银B2C收单"),
    RECEIVE_WECHAT_H5(2, "微信支付收单"),

    PAYMENT_ADVANCE(3, "垫资代付"),

    SETTLE_ADVANCE_CLEAR(4, "垫资账户清零"),
    SETTLE_SETTLE_TO_ACCOUNT(5, "结算到账"),
    SETTLE_REMIT(6, "结算打款"),

    ADVANCE_ADJUST(7, "垫资调整"),
    ;

    /** 枚举值 */
    private int value;

    /** 描述 */
    private String desc;

    private BusinessCodeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
