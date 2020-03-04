package com.xpay.api.base.utils;

import com.xpay.api.base.params.MerchantInfo;
import com.xpay.api.base.annonation.NotSign;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import com.xpay.common.statics.enums.common.SignTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 签名、验签的工具类
 *
 * @author chenyf
 * @date 2018-12-15
 */
public class SignUtil {
    public final static boolean IS_SHA = true;
    public final static boolean IS_EMPTY_NOT_SIGN = false;

    public final static String SIGN_SEPARATOR = "&";//分隔符
    public final static String SIGN_EQUAL = "=";//等于号
    public final static String SIGN_KEY_PARAM_NAME = "key";


    /**
     * 验证签名
     *
     * @param requestParam
     * @param merchantInfo
     * @return
     */
    public static boolean verify(RequestParam requestParam, MerchantInfo merchantInfo) {
        String signStr = getSortedString(requestParam, IS_EMPTY_NOT_SIGN);
        if (String.valueOf(SignTypeEnum.RSA.getValue()).equals(requestParam.getSign_type())) {
            return RSAUtil.verify(signStr, merchantInfo.getSignValidKey(), requestParam.getSign(), IS_SHA);
        } else if (String.valueOf(SignTypeEnum.MD5.getValue()).equals(requestParam.getSign_type())) {
            signStr = genMD5Sign(signStr, merchantInfo.getSignValidKey());
            return signStr.equals(requestParam.getSign());
        } else {
            throw new BizException("验签失败，未预期的签名类型：" + requestParam.getSign_type());
        }
    }

    /**
     * 生成签名
     *
     * @param responseParam
     * @param merchantInfo
     * @return
     */
    public static void sign(ResponseParam responseParam, MerchantInfo merchantInfo) {
        if (responseParam == null) {
            return;
        } else if (StringUtil.isEmpty(responseParam.getMch_no()) || StringUtil.isEmpty(responseParam.getSign_type())) {
            responseParam.setSign("");
            return;
        }
        if (StringUtil.isEmpty(responseParam.getRand_str())) {
            responseParam.setRand_str(RandomUtil.get32LenStr());
        }

        String signStr = getSortedString(responseParam, IS_EMPTY_NOT_SIGN);
        String signResult;
        if (String.valueOf(SignTypeEnum.RSA.getValue()).equals(responseParam.getSign_type())) {
            signResult = RSAUtil.sign(signStr, merchantInfo.getSignGenKey(), IS_SHA);
        } else if (String.valueOf(SignTypeEnum.MD5.getValue()).equals(responseParam.getSign_type())) {
            signResult = genMD5Sign(signStr, merchantInfo.getSignGenKey());
        } else {
            //抛出签名失败的异常
            throw new BizException("签名失败，未预期的签名类型：" + responseParam.getSign_type());
        }

        responseParam.setSign(signResult);
    }

    public static String sign(Object obj, String signType, String priKey) {
        String signStr = getSortedString(obj, IS_EMPTY_NOT_SIGN);
        String signResult;
        if (String.valueOf(SignTypeEnum.RSA.getValue()).equals(signType)) {
            signResult = RSAUtil.sign(signStr, priKey, IS_SHA);
        } else if (String.valueOf(SignTypeEnum.MD5.getValue()).equals(signType)) {
            signResult = genMD5Sign(signStr, priKey);
        } else {
            //抛出签名失败的异常
            throw new BizException("签名失败，未预期的签名类型：" + signType);
        }
        return signResult;
    }

    /**
     * 验签
     *
     * @param obj       待验签对象
     * @param signedStr 已签名的字符串
     * @param signType  签名类型
     * @param pubKey    验签公钥
     * @return
     */
    public static boolean verify(Object obj, String signedStr, String signType, String pubKey) {
        String signStr = getSortedString(obj, IS_EMPTY_NOT_SIGN);
        if (String.valueOf(SignTypeEnum.RSA.getValue()).equals(signType)) {
            return RSAUtil.verify(signStr, pubKey, signedStr, IS_SHA);
        } else if (String.valueOf(SignTypeEnum.MD5.getValue()).equals(signType)) {
            signStr = genMD5Sign(signStr, pubKey);
            return signStr.equals(signedStr);
        } else {
            throw new BizException("验签失败，未预期的签名类型：" + pubKey);
        }
    }

    private static String getSortedString(Object obj, boolean ignoreEmpty) {
        Field[] fields = obj.getClass().getDeclaredFields();
        //treeMap里的key 是按照字典序排序的
        Map<String, Object> signMap = new TreeMap<>();
        for (Field filed : fields) {
            String name = filed.getName();
            NotSign notSign = filed.getAnnotation(NotSign.class);
            if (notSign != null) {//不参与签名或验签的参数直接跳过
                continue;
            }

            Object value;
            try {
                filed.setAccessible(true);
                value = filed.get(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (ignoreEmpty && value == null) {//忽略空值
                continue;
            }
            signMap.put(name, value);
        }

        StringBuilder content = new StringBuilder();
        signMap.forEach((key, value) -> {
            String signValue;
            if (value == null) {
                signValue = "";
            } else if (value instanceof String) {
                signValue = (String) value;
            } else {
                signValue = JsonUtil.toString(value);
            }

            content.append(key).append(SIGN_EQUAL);
            content.append(signValue);
            content.append(SIGN_SEPARATOR);
        });
        content.deleteCharAt(content.lastIndexOf(SIGN_SEPARATOR));

        return content.toString();
    }

    private static String genMD5Sign(String signStr, String key) {
        return CodeUtil.base64Encode(MD5Util.getMD5(signStr + SIGN_SEPARATOR + SIGN_KEY_PARAM_NAME + SIGN_EQUAL + key));
    }

    public static void main(String[] args) throws Exception {
//        String secretKey = "12345678qwertyui";
//        String randStr = RandomUtil.get32LenStr();
//        randStr = "099F34608FD3738A599497C5E054AFBA";
//
//        RequestParam requestParam = new RequestParam();
//        requestParam.setMch_no("888000000000000");
//        requestParam.setMethod("joinpay.trade.singleCreate");
//        requestParam.setVersion("1.0");
//        requestParam.setRand_str(randStr);
//        requestParam.setSign_type(SignTypeEnum.MD5.getValue());
//
//        Map<String, String> dataMap = new HashMap<>();
//        dataMap.put("product_code", "EL321223345558");
//        dataMap.put("product_name", "电磁炉");
//        dataMap.put("price", "28000");
//        requestParam.setData(JsonUtil.toString(dataMap));
//
//        String signStr = getSortedString(requestParam);
//
//        if(SignTypeEnum.MD5.getValue().equals(requestParam.getSign_type())){
//            signStr = genMD5Sign(signStr, secretKey);
//        }else if(SignTypeEnum.RSA.getValue().equals(requestParam.getSign_type())){
//            signStr = RSAUtil.sign(signStr, secretKey);
//        }else{
//            signStr = "";
//            System.out.println("未预期的签名类型："+requestParam.getSign_type());
//        }
//        requestParam.setSign(signStr);
//
//        System.out.println("sign.length() = " + requestParam.getSign().length());
//        System.out.println("RequestParam = " + JsonUtil.toString(requestParam));
    }
}
