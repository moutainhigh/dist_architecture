package com.xpay.common.statics.enums.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:账务处理结果回调阶段枚举
 * @author: chenyf
 * @Date: 2018/3/16
 */
public enum AccountProcessResultCallbackStageEnum {
    PENDING("待发送", 1),

    SENT("已发送", 2),

    NONE_SEND("不发送", 3);

    /** 枚举值 */
    private int value;

    /** 描述 */
    private String desc;

    private AccountProcessResultCallbackStageEnum(String desc, int value) {
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

    /**
     * 根据枚举值获取枚举属性.
     *
     * @param value
     *            枚举值.
     * @return enum 枚举属性.
     */
    public static AccountProcessResultCallbackStageEnum getEnum(int value) {
        AccountProcessResultCallbackStageEnum resultEnum = null;
        AccountProcessResultCallbackStageEnum[] enumAry = AccountProcessResultCallbackStageEnum.values();
        for (int num = 0; num < enumAry.length; num++) {
            if (enumAry[num].getValue() == value) {
                resultEnum = enumAry[num];
                break;
            }
        }
        return resultEnum;
    }

    /**
     * 将枚举类转换为map.
     *
     * @return Map<key, Map<attr, value>>
     */
    public static Map<String, Map<String, Object>> toMap() {
        AccountProcessResultCallbackStageEnum[] ary = AccountProcessResultCallbackStageEnum.values();
        Map<String, Map<String, Object>> enumMap = new HashMap<String, Map<String, Object>>();
        for (int num = 0; num < ary.length; num++) {
            Map<String, Object> map = new HashMap<String, Object>();
            String key = String.valueOf(getEnum(ary[num].getValue()));
            map.put("value", ary[num].getValue());
            map.put("desc", ary[num].getDesc());
            enumMap.put(key, map);
        }
        return enumMap;
    }

    public static List toList() {
        AccountProcessResultCallbackStageEnum[] ary = AccountProcessResultCallbackStageEnum.values();
        List list = new ArrayList();
        for (int i = 0; i < ary.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("value", ary[i].toString());
            map.put("desc", ary[i].getDesc());
            list.add(map);
        }
        return list;
    }
}
