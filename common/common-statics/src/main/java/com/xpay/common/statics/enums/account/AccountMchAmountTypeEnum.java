package com.xpay.common.statics.enums.account;

/**
 * @Description:平台商户账户金额类型 todo 是否可删除
 * @author: chenyf
 * @Date: 2019/9/2
 */
public enum AccountMchAmountTypeEnum {
    /**
     * 已结算金额
     */
    SETTLED_AMOUNT(1, "已结算金额"),

    /**
     * 待结算金额
     */
    UNSETTLE_AMOUNT(2, "待结算金额"),

    /**
     * 总垫资金额
     */
    TOTAL_ADVANCE_AMOUNT(3,"总垫资金额"),

    /**
     * 可用垫资金额
     */
    AVAILABLE_ADVANCE_AMOUNT(4,"可用垫资金额"),

    /**
     * 已结算金额 + 可垫资金额 （主要在扣款时使用）
     */
    SETTLE_AND_AVAILABLE_ADVANCE_AMOUNT(5,"已结加可垫金额"),

    /**
     * 使用已结算金额 或 可垫资金额（主要在扣款时使用）
     */
    SETTLED_OR_AVAILABLE_ADVANCE_AMOUNT(6, "已结或可垫金额"),

    /**
     * 使用可用余额，即：已结 + 可垫 + 留存 （主要在退款扣款时使用）
     */
    AVAILABLE_BALANCE_AMOUNT(7,"可用余额"),

    /**
     * 风控金额
     */
    RSM_AMOUNT(8, "风控金额"),

    /**
     * 原出款金额（出款退回时使用）
     */
    SOURCE_DEBIT_AMOUNT(9, "原出款金额"),

    /**
     * 待结算转移到已结算
     */
    UNSETTLE_CIRCULATE_SETTLE_AMOUNT(10, "待结算到已结算"),
    ;

    /** 枚举值 */
    private int value;

    /** 描述 */
    private String desc;

    private AccountMchAmountTypeEnum(int value, String desc) {
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
