package com.xpay.common.util.utils;

/**
 * Author: Cmf
 * Date: 2019/10/22
 * Time: 10:59
 * Description: 基本数据转换类
 */
public class ConvertUtil {

    /**
     * 转换成Integer,
     * 若转换失败，则返回null
     *
     * @param str .
     * @return
     */
    public static Integer stringToInteger(String str) {
        try {
            return StringUtil.isEmpty(str) ? null : Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 若转换失败，则返回Null
     *
     * @param str .
     * @return
     */
    public static Long stringToLong(String str) {
        try {
            return StringUtil.isEmpty(str) ? null : Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 若转换失败，则返回null.
     *
     * @param str .
     * @return
     */
    public static Double stringToDouble(String str) {
        try {
            return StringUtil.isEmpty(str) ? null : Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
