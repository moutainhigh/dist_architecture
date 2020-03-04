package com.xpay.common.util.utils;

import com.xpay.common.statics.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加解密的工具类
 * 可参考 https://segmentfault.com/a/1190000011263680
 */
public class RSAUtil {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);
    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1withRSA";
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String RSA = "RSA";

    public static final String ANDROID_ENCRYPT_ALGORITHM = "RSA/ECB/NoPadding";
    public static final String DEFAULT_ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding";

    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 116;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 初始化BC提供者，RSA算法必须要先初始化BC才能进行加密解密和签名验签
     */
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 生成RSA签名串
     *
     * @param data       需要生成签名串的数据
     * @param privateKey 私钥
     * @return
     * @throws BizException
     */
    public static String sign(String data, String privateKey, boolean isSha) throws BizException {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = CodeUtil.base64Decode(privateKey);
            String algorithm = isSha ? SIGNATURE_ALGORITHM_SHA1 : SIGNATURE_ALGORITHM_MD5;

            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey priKey = KeyFactory.getInstance(RSA).generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            signature.update(dataBytes);
            return CodeUtil.base64Encode(signature.sign());
        } catch (Throwable e) {
            logger.error("==>sign err:", e);
            throw new BizException("生成RSA签名失败", e);
        }
    }

    /**
     * 验证RSA签名串
     *
     * @param data      需要验签的数据
     * @param publicKey 公钥
     * @param sign      用户传过来的签名串
     * @return
     * @throws BizException
     */
    public static boolean verify(String data, String publicKey, String sign, boolean isSha) throws BizException {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] signBytes = CodeUtil.base64Decode(sign);
            String algorithm = isSha ? SIGNATURE_ALGORITHM_SHA1 : SIGNATURE_ALGORITHM_MD5;
            PublicKey publicK = parsePublicKey(publicKey);

            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicK);
            signature.update(dataBytes);
            return signature.verify(signBytes);
        } catch (Throwable e) {
            logger.error("==>verify err:", e);
            throw new BizException("RSA验签失败", e);
        }
    }

    /**
     * 对称密钥公钥加密
     *
     * @param content      需要加密的明文内书
     * @param publicKeyStr 公钥
     * @return 加密密文
     */
    public static String encryptByPublicKey(String content, String publicKeyStr) {
        try {
            byte[] dataBytes = content.getBytes(StandardCharsets.UTF_8);
            dataBytes = doCipher(dataBytes, publicKeyStr, true, false);
            return CodeUtil.base64Encode(dataBytes);
        } catch (Throwable e) {
            throw new BizException("RSA加密失败", e);
        }
    }

    /**
     * 对称密钥密文解密
     *
     * @param content       需要解密的内容
     * @param privateKeyStr 私钥
     * @return 对称密钥明文
     * @throws Exception
     */
    public static String decryptByPrivateKey(String content, String privateKeyStr) {
        try {
            byte[] dataBytes = CodeUtil.base64Decode(content);
            dataBytes = doCipher(dataBytes, privateKeyStr, false, false);
            return new String(dataBytes, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new BizException("RSA解密失败", e);
        }
    }

    private static byte[] doCipher(byte[] dataBytes, String keyStr, boolean isEncrypt, boolean isAndroid) throws Exception {
        Cipher cipher;
        int maxBlock;

        if (isEncrypt) {
            maxBlock = MAX_ENCRYPT_BLOCK;
            Key key = parsePublicKey(keyStr);
            if (isAndroid) {
                cipher = Cipher.getInstance(ANDROID_ENCRYPT_ALGORITHM);// 如果是安卓机
            } else {
                cipher = Cipher.getInstance(DEFAULT_ENCRYPT_ALGORITHM);
            }
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            maxBlock = MAX_DECRYPT_BLOCK;
            byte[] keyBytes = CodeUtil.base64Decode(keyStr);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
        }

        int offSet = 0, i = 0, inputLen = dataBytes.length;
        byte[] cache;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // 对数据分段加密/解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > maxBlock) {
                    cache = cipher.doFinal(dataBytes, offSet, maxBlock);
                } else {
                    cache = cipher.doFinal(dataBytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * maxBlock;
            }
            return out.toByteArray();
        } finally {
            out.close();
        }
    }

    /**
     * 生成公私密钥对
     *
     * @return
     */
    public static Map<String, String> genKeyPair() throws BizException {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            Map<String, String> keyMap = new HashMap<>(2);
            keyMap.put(PUBLIC_KEY, CodeUtil.base64Encode(keyPair.getPublic().getEncoded()));
            keyMap.put(PRIVATE_KEY, CodeUtil.base64Encode(keyPair.getPrivate().getEncoded()));
            return keyMap;
        } catch (Throwable e) {
            throw new BizException("生成RSA密钥对出现异常", e);
        }
    }

    public static RSAPublicKey parsePublicKey(String publicKeyBase64) throws Exception {
        byte[] keyBytes = CodeUtil.base64Decode(publicKeyBase64);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    public static RSAPrivateKey parsePrivateKey(String privateKeyBase64) throws Exception {
        byte[] keyBytes = CodeUtil.base64Decode(privateKeyBase64);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 校验是否是有效的公钥BASE64字符串
     *
     * @param publicKeyBase64 .
     * @return .
     */
    public static boolean validPublicKey(String publicKeyBase64) {
        try {
            parsePublicKey(publicKeyBase64);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 校验是否是有效的私钥BASE64字符串
     *
     * @param privateKeyBase64 .
     * @return .
     */
    public static boolean validPrivateKey(String privateKeyBase64) {
        try {
            parsePrivateKey(privateKeyBase64);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
