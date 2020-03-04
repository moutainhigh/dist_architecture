package com.xpay.common.statics.enums.account;

import java.util.Arrays;

/**
 * 账务处理结果审核枚举
 */
public enum AccountResultAuditTypeEnum {
    AUDIT_TO_SUCCESS(1, "审核为成功"),
    AUDIT_TO_FAIL(2, "审核为失败"),
    AUDIT_TO_REPROCESS(3, "审核为重新处理"),
    ;

    private int value;

    /**
     * 描述
     */
    private String desc;

    AccountResultAuditTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }


    public static AccountResultAuditTypeEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElse(null);
    }

}
