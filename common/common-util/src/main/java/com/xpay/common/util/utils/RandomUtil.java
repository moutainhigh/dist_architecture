package com.xpay.common.util.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @Description:
 * @author: chenyf
 * @Date: 2018/2/7
 */
public class RandomUtil {
    /**
     * 产生16长度的随机字符串
     * @return
     */
    public static String get16LenStr(){
        if(getInt(2) == 0){
            return MD5Util.getMD5Hex(UUID.randomUUID().toString()).substring(16);
        }else{
            return MD5Util.getMD5Hex(UUID.randomUUID().toString()).substring(1, 17);
        }
    }

    /**
     * 产生32长度的随机字符串
     * @return
     */
    public static String get32LenStr(){
        return MD5Util.getMD5Hex(UUID.randomUUID().toString());
    }

    /**
     * 产生 min ~ max 之间的随机数
     * @param min
     * @param max
     * @return
     */
    public static int getInt(int min, int max){
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * 产生 0 ~ max 之间的随机数
     * @param max
     * @return
     */
    public static int getInt(int max){
        Random random = new Random();
        return random.nextInt(max);
    }

    /**
     * 产生 count 长度的随机整型字符串
     * @param count
     * @return
     */
    public static String getDigitStr(int count){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < count; i++){
            int value = getInt(9);
            sb.append(value);
        }
        return sb.toString();
    }

    public static void main(String[] args){
        String str = getDigitStr(1);
        System.out.println("str = " + str);
        str = getDigitStr(2);
        System.out.println("str = " + str);
        str = getDigitStr(3);
        System.out.println("str = " + str);
        str = getDigitStr(4);
        System.out.println("str = " + str);
        str = getDigitStr(6);
        System.out.println("str = " + str);
    }
}
