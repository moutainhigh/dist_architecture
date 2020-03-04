package com.xpay.common.statics.enums.user.pms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 10:31
 * Description: 操作员状态
 */
public enum PmsOperatorStatusEnum {

    ACTIVE("激活", 1),
    INACTIVE("冻结", -1),
    UNAUDITED("未审核", 2);

    /**
     * 描述
     */
    private String desc;
    /**
     * 枚举值
     */
    private int value;

    PmsOperatorStatusEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public String getDesc() {
        return desc;
    }


    public static PmsOperatorStatusEnum getEnum(int value) {
        return Arrays.stream(PmsOperatorStatusEnum.values()).filter(p -> p.getValue() == value).findFirst().orElse(null);
    }

    public static List<Map<String, Object>> toList() {
        return Arrays.stream(PmsOperatorStatusEnum.values()).map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("value", p.value);
            item.put("desc", p.desc);
            return item;
        }).collect(Collectors.toList());
    }

}
