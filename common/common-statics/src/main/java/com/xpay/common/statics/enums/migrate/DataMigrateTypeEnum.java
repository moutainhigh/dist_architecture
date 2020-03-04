package com.xpay.common.statics.enums.migrate;

public enum DataMigrateTypeEnum {
    ACCOUNT_MCH_PROCESS_DETAIL(1, "平台商户账务处理明细"),
    ACCOUNT_MCH_PROCESS_PENDING(2, "平台商户待账务处理"),
    ACCOUNT_MCH_PROCESS_RESULT(3, "平台商户账务处理结果"),
    ACCOUNT_MCH_COMMON_UNIQUE(4, "平台商户账务唯一约束"),


    ACCOUNT_SUB_PROCESS_DETAIL(5, "子商户账务处理明细"),
    ACCOUNT_SUB_PROCESS_PENDING(6, "子商户待账务处理"),
    ACCOUNT_SUB_PROCESS_RESULT(7, "子商户账务处理结果"),
    ACCOUNT_SUB_COMMON_UNIQUE(8, "子商户账务唯一约束"),


    ACCOUNT_TRANSIT_PROCESS_DETAIL(9, "在途账户账务处理明细"),
    ACCOUNT_TRANSIT_PROCESS_PENDING(10, "在途账户待账务处理"),
    ACCOUNT_TRANSIT_PROCESS_RESULT(11, "在途账户账务处理结果"),
    ACCOUNT_TRANSIT_COMMON_UNIQUE(12, "在途账户账务唯一约束"),

    ;

    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;

    private DataMigrateTypeEnum(int value, String desc) {
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

    public static DataMigrateTypeEnum getEnum(String name) {
        DataMigrateTypeEnum[] values = DataMigrateTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].name().equals(name)) {
                return values[i];
            }
        }
        return null;
    }
}
