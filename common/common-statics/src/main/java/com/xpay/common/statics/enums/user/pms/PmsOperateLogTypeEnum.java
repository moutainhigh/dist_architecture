package com.xpay.common.statics.enums.user.pms;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 16:06
 * Description:运营后台操作员操作日志-操作类型
 */
public enum PmsOperateLogTypeEnum {

    LOGIN("登录", 1),
    LOGOUT("登出", 2),
    CREATE("添加数据", 3),
    MODIFY("修改数据", 4),
    DELETE("删除数据", 5),
    QUERY("查询数据", 6),

    ;

    /**
     * 描述
     */
    private String desc;
    /**
     * 枚举值
     */
    private int value;

    PmsOperateLogTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public String getDesc() {
        return desc;
    }

}
