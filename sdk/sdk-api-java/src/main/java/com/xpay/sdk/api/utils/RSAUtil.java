package com.xpay.sdk.api.utils;

import com.xpay.sdk.api.exceptions.SDKException;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加解密的工具类
 */
public class RSAUtil {
    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1withRSA";
    public static final String RSA = "RSA";
    public static final String ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding";

    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 116;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 初始化BC提供者
     */
    static{
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * 生成RSA签名串
     *
     * @param data       需要生成签名串的数据
     * @param privateKey 私钥
     * @return
     */
    public static String sign(String data, String privateKey) throws SDKException {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = CodeUtil.base64Decode(privateKey);

            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            PrivateKey priKey = KeyFactory.getInstance(RSA).generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA1);
            signature.initSign(priKey);
            signature.update(dataBytes);
            return CodeUtil.base64Encode(signature.sign());
        }catch (Throwable e){
            throw new SDKException("生成RSA签名失败", e);
        }
    }

    /**
     * 验证RSA签名串
     *
     * @param data      需要验签的数据
     * @param publicKey 公钥
     * @param signOri   原签名串
     * @return
     */
    public static boolean verify(String data, String publicKey, String signOri) throws SDKException {
        try {
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] signBytes = CodeUtil.base64Decode(signOri);
            PublicKey publicK = getPublicKey(publicKey);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM_SHA1);
            signature.initVerify(publicK);
            signature.update(dataBytes);
            return signature.verify(signBytes);
        } catch (Throwable e) {
            throw new SDKException("RSA验签失败", e);
        }
    }

    /**
     * 公钥加密
     * @param content      明文内容
     * @param publicKeyStr 公钥
     * @return 加密密文
     */
    public static String encryptByPublicKey(String content, String publicKeyStr) {
        try {
            byte[] dataBytes = content.getBytes(StandardCharsets.UTF_8);
            int maxBlock = MAX_ENCRYPT_BLOCK;
            Key key = getPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            dataBytes = doCipher(cipher, dataBytes, maxBlock);
            return CodeUtil.base64Encode(dataBytes);
        } catch (Throwable e) {
            throw new SDKException("RSA加密失败",e);
        }
    }

    /**
     * 密钥解密
     *  @param content          需要解密的密文内容
     * @param privateKeyStr     私钥字符串
     * @return 对称密钥明文
     * @throws Exception
     */
    public static String decryptByPrivateKey(String content, String privateKeyStr) {
        try {
            byte[] dataBytes = CodeUtil.base64Decode(content);
            int maxBlock = MAX_DECRYPT_BLOCK;
            byte[] keyBytes = CodeUtil.base64Decode(privateKeyStr);

            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);

            dataBytes = doCipher(cipher, dataBytes, maxBlock);
            return new String(dataBytes, StandardCharsets.UTF_8);
        } catch (Throwable e) {
            throw new SDKException("RSA解密失败", e);
        }
    }

    /**
     * 生成公私密钥对
     * @return
     */
    public static Map<String, String> genKeyPair() throws SDKException {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            Map<String, String> keyMap = new HashMap<>(2);
            keyMap.put(PUBLIC_KEY, CodeUtil.base64Encode(keyPair.getPublic().getEncoded()));
            keyMap.put(PRIVATE_KEY, CodeUtil.base64Encode(keyPair.getPrivate().getEncoded()));
            return keyMap;
        }catch (Throwable e){
            throw new SDKException("生成RSA密钥对出现异常", e);
        }
    }

    private static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = CodeUtil.base64Decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 分段加解密
     * @param cipher
     * @param dataBytes
     * @param maxBlock
     * @return
     * @throws Exception
     */
    private static byte[] doCipher(Cipher cipher, byte[] dataBytes, int maxBlock) throws Exception {
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


    public static void main(String[] args){
        Map<String, String> keyMap = genKeyPair();
        System.out.println(JsonUtil.toString(keyMap));
    }
}
