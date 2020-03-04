package com.xpay.sdk.api.utils;

import com.xpay.sdk.api.exceptions.SDKException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class MD5Util {

    /**
     * 生成16进制的MD5字符串
     * @param str
     * @return
     */
    public static String getMD5Hex(String str) {
        return byte2Hex(getMD5(str));
    }

    public static byte[] getMD5(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            if (StringUtil.isNotEmpty(str)) {
                messageDigest.update(str.getBytes("UTF-8"));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new SDKException("生成MD5信息时异常", e);
        } catch (UnsupportedEncodingException e) {
            throw new SDKException("生成MD5信息时异常", e);
        }
        return messageDigest.digest();
    }

    public static String byte2Hex(byte[] bytes) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int j = bytes.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (byte byte0 : bytes) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
