package com.xpay.common.statics.enums.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账务明细清算阶段
 * 
 * Created by jo on 2017/9/5.
 */
public enum AccountDetailClearStageEnum {
    /**
     * 不清算
     */
    NONE(1, "不清算"),

    /**
     * 待清算
     */
    PENDING(2,"待清算"),

    /**
     * 已清算
     */
    FINISH(4,"已清算"),

    ;

    /** 枚举值 */
    private int value;

    /** 描述 */
    private String desc;

    private AccountDetailClearStageEnum(int value, String desc) {
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
    public static AccountDetailClearStageEnum getEnum(int value) {
        AccountDetailClearStageEnum resultEnum = null;
        AccountDetailClearStageEnum[] enumAry = AccountDetailClearStageEnum.values();
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
        AccountDetailClearStageEnum[] ary = AccountDetailClearStageEnum.values();
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

    /**
     * 将枚举类转换为list.
     *
     * @return List<Map<String, Object>> list.
     */
    public static List<Map<String, Object>> toList() {
        AccountDetailClearStageEnum[] ary = AccountDetailClearStageEnum.values();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < ary.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("value", ary[i].getValue());
            map.put("desc", ary[i].getDesc());
            list.add(map);
        }
        return list;
    }
}
