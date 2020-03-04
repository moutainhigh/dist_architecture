package com.xpay.common.statics.enums.user.portal;

import java.util.Arrays;
import java.util.Objects;

/**
 * Author: Cmf
 * Date: 2019.11.19
 * Time: 11:40
 * Description: 商户操作员角色类型
 */
public enum PortalRoleTypeEnum {
    /**
     * 商户管理员
     **/
    PORTAL_ADMIN("商户管理员", 1),

    /**
     * 商户操作员
     **/
    PORTAL_USER("商户操作员", 2);


    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;


    PortalRoleTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }


    public static PortalRoleTypeEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> Objects.equals(value, p.getValue())).findFirst().orElse(null);
    }

}
