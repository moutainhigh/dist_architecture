package com.xpay.sdk.api.utils;

import com.xpay.sdk.api.exceptions.SDKException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * AES加解密工具类
 * @author chenyf
 * @date 2018-12-15
 */
public class AESUtil {
    public final static String ALG_AES = "AES";
    private static final String ECB_MODE = "AES/ECB/PKCS5Padding";//算法/模式/补码方式

    static{
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static String encryptECB(String content, String secKey){
        try {
            Cipher cipher = Cipher.getInstance(ECB_MODE);
            SecretKeySpec secSpec = genSecretKeySpec(secKey);
            cipher.init(Cipher.ENCRYPT_MODE, secSpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return CodeUtil.base64Encode(encrypted);
        }catch(Throwable e){
            throw new SDKException("AES加密异常", e);
        }
    }

    public static String decryptECB(String content, String secKey){
        try {
            Cipher cipher = Cipher.getInstance(ECB_MODE);
            SecretKeySpec secSpec = genSecretKeySpec(secKey);
            cipher.init(Cipher.DECRYPT_MODE, secSpec);
            byte[] encrypted1 = CodeUtil.base64Decode(content);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        }catch(Throwable e){
            throw new SDKException("AES解密异常", e);
        }
    }

    /**
     * 生成密钥对象 密钥可支持16位或32位，如果是32位，可能会报：java.security.InvalidKeyException: Illegal key size 异常，此时需要更换JDK的local_policy.jar和US_export_policy.jar
     * @param secKey
     * @return
     * @throws Exception
     */
    public static SecretKeySpec genSecretKeySpec(String secKey) {
        if (secKey == null || (secKey.length() != 16)) {
            throw new SDKException("密钥长度须为16位");
        }
        return new SecretKeySpec(secKey.getBytes(StandardCharsets.UTF_8), ALG_AES);
    }
}
