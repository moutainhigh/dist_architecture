package com.xpay.common.statics.enums.user.portal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 10:31
 * Description: 商户操作员状态
 */
public enum PortalOperatorStatusEnum {

    ACTIVE("审核通过", 100),
    INACTIVE("冻结", 101),
    UNAUDITED("未审核", 102);

    /**
     * 描述
     */
    private String desc;
    /**
     * 枚举值
     */
    private int value;

    PortalOperatorStatusEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public String getDesc() {
        return desc;
    }


    public static PortalOperatorStatusEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> p.getValue() == value).findFirst().orElse(null);
    }

    public static List<Map<String, Object>> toList() {
        return Arrays.stream(values()).map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("value", p.value);
            item.put("desc", p.desc);
            return item;
        }).collect(Collectors.toList());
    }

}
