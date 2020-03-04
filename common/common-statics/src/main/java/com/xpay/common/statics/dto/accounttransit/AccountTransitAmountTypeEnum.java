package com.xpay.common.statics.dto.accounttransit;

/**
 * Author: Cmf
 * Date: 2019.12.19
 * Time: 14:20
 * Description: 在途账户金额类型
 */
public enum AccountTransitAmountTypeEnum {
    /**
     * 在途余额
     */
    TRANSIT_AMOUNT(1, "在途余额"),

    /**
     * 原出款金额（出款退回时使用）
     */
    SOURCE_DEBIT_AMOUNT(9, "原出款金额"),

    ;
    /**
     * 枚举值
     */
    private int value;
    /**
     * 描述
     */
    private String desc;

    AccountTransitAmountTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return value;
    }
}
