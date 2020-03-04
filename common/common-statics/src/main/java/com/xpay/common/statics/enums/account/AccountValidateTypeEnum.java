package com.xpay.common.statics.enums.account;

public enum AccountValidateTypeEnum {
    /**
     * 入账
     */
    CREDIT(1, "入账"),
    /**
     * 冻结
     */
    FROZEN(2,"冻结");

    /** 枚举值 */
    private int value;
    /** 描述 */
    private String desc;


    private AccountValidateTypeEnum(int value, String desc) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
