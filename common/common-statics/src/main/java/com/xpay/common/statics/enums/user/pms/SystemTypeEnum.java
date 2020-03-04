package com.xpay.common.statics.enums.user.pms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统类型
 *
 * @author longfenghua
 * @date 2019/11/15
 */
public enum SystemTypeEnum {
    COMMON_MANAGEMENT("通用", 1),
    BOSS_MANAGEMENT("运营后台", 2),
    MERCHANT_MANAGEMENT("商户后台", 3),
    ;

    /**
     * 描述
     */
    private String desc;

    /**
     * 枚举值
     */
    private int value;

    SystemTypeEnum(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public int getValue() {
        return value;
    }

    public static List<Map<String, Object>> toList() {
        return Arrays.stream(SystemTypeEnum.values()).map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("value", p.value);
            item.put("desc", p.desc);
            return item;
        }).collect(Collectors.toList());
    }
}
