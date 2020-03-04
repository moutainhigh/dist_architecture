package com.xpay.common.statics.enums.user.pms;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作员类型
 */
public enum PmsOperatorTypeEnum {

    /**
     * 超级管理员
     **/
    ADMIN("超级管理员", 1),

    /**
     * 普通用户
     **/
    USER("普通用户", 2);


    /**
     * 枚举值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;


    PmsOperatorTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }


    public static PmsOperatorTypeEnum getEnum(int value) {
        return Arrays.stream(values()).filter(p -> Objects.equals(value, p.getValue())).findFirst().orElse(null);
    }

    public static List<Map<String, Object>> toList() {
        return Arrays.stream(values()).map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("value", p.value);
            item.put("desc", p.desc);
            return item;
        }).collect(Collectors.toList());
    }

    public static Map<String, Map<String, Object>> toMap() {
        return Arrays.stream(PmsOperatorTypeEnum.values())
                .collect(Collectors.toMap(Enum::name, p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("value", p.value);
                    item.put("desc", p.desc);
                    return item;
                }));
    }
}
