package com.xpay.common.statics.enums.account;

/**
 * Author: Cmf
 * Date: 2019.12.19
 * Time: 10:44
 * Description: 账务处理扣款明细的确认状态
 */
public enum AccountDebitCommitStageEnum {
    UN_COMMITTED(1, "待确认"),
    DEBIT_COMMITTED(2, "已确认扣款"),
    RETURN_COMMITTED(3, "已确认退回"),
    NO_NEED_COMMIT(4, "无须确认"),
    ;
    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;

    AccountDebitCommitStageEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
