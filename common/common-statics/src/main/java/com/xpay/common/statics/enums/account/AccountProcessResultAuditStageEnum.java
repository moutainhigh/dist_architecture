package com.xpay.common.statics.enums.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 账务处理结果审核结算
 * @author: chenyf
 * @Date: 2018/3/14
 */
public enum AccountProcessResultAuditStageEnum {
    AUDIT_NONE("不审核", 1),

    AUDIT_WAITING("待审核", 2),

    AUDIT_FINISHED("已审核", 3);

    /** 枚举值 */
    private int value;

    /** 描述 */
    private String desc;

    private AccountProcessResultAuditStageEnum(String desc, int value) {
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
    public static AccountProcessResultAuditStageEnum getEnum(int value) {
        AccountProcessResultAuditStageEnum resultEnum = null;
        AccountProcessResultAuditStageEnum[] enumAry = AccountProcessResultAuditStageEnum.values();
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
        AccountProcessResultAuditStageEnum[] ary = AccountProcessResultAuditStageEnum.values();
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
        AccountProcessResultAuditStageEnum[] ary = AccountProcessResultAuditStageEnum.values();
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
