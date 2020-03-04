package com.xpay.common.statics.enums.message;

/**
 * 系统日常运营团队枚举类
 */
public enum GroupKeyEnum {
    GLOBAL_LOCK(1, "全局锁预警"),

    ;

    private int value;
    private String desc;

    private GroupKeyEnum(int value, String desc){
        this.value = value;
        this.desc = desc;
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

    public static GroupKeyEnum getEnum(String name){
        return GroupKeyEnum.valueOf(name);
    }
}
