package com.xpay.common.statics.enums.user.pms;

/**
 * 功能类型枚举
 *
 * @author linguangsheng
 * @date 2019/11/1
 */
public enum PmsFunctionTypeEnum {

    /**
     * 菜单项
     */
    MENU_TYPE("菜单项", 1),

    /**
     * 操作项
     */
    ACTION_TYPE("操作项", 2),

    ;

    /**
     * 描述
     */
    private String desc;

    /**
     * 枚举值
     */
    private int value;

    PmsFunctionTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return value;
    }
}
