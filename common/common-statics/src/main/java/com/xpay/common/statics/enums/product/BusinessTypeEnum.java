package com.xpay.common.statics.enums.product;

public enum BusinessTypeEnum {
    PAY_RECEIVE(1, "收单"),
    REFUND(2, "退款"),
    LIQUIDATION(3, "清算"),
    SETTLE(4,"结算"),
    PAYMENT(5, "付款"),
    REMIT_RETURN(6, "退汇"),
    RCMS(7, "风控"),


    ;

    /** 枚举值 */
    private int value;

    /** 描述 */
    private String desc;

    private BusinessTypeEnum(int value, String desc) {
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
