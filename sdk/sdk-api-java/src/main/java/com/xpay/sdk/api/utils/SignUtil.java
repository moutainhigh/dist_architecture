package com.xpay.sdk.api.utils;

import com.xpay.sdk.api.entity.Callback;
import com.xpay.sdk.api.entity.Request;
import com.xpay.sdk.api.entity.Response;
import com.xpay.sdk.api.enums.SignType;
import com.xpay.sdk.api.exceptions.SDKException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 签名、验签的工具类
 * @author chenyf
 * @date 2018-12-15
 */
public class SignUtil {
    public final static String SIGN_SEPARATOR = "&";//分隔符
    public final static String SIGN_EQUAL = "=";//等于号
    public final static String SIGN_KEY_PARAM_NAME = "key";
    public final static List<String> NOT_SIGN_PARAM = Arrays.asList(new String[]{"sign","sec_key"});//不参与签名/验签的参数

    /**
     * 同步响应时验证签名
     * @param response
     * @param key
     * @return
     */
    public static boolean verify(Response response, String key){
        String signStr = getSortedString(response);
        if(SignType.RSA.getValue().equals(response.getSign_type())){
            return RSAUtil.verify(signStr, key, response.getSign());
        }else if(SignType.MD5.getValue().equals(response.getSign_type())){
            signStr = genMD5Sign(signStr, key);
            return signStr.equals(response.getSign());
        }else{
            return false;
        }
    }

    /**
     * 异步回调时验证签名
     * @param callback
     * @param key
     * @return
     */
    public static boolean verify(Callback callback, String key){
        String signStr = getSortedString(callback);
        if(SignType.RSA.getValue().equals(callback.getSign_type())){
            return RSAUtil.verify(signStr, key, callback.getSign());
        }else if(SignType.MD5.getValue().equals(callback.getSign_type())){
            signStr = genMD5Sign(signStr, key);
            return signStr.equals(callback.getSign());
        }else{
            return false;
        }
    }

    /**
     * 生成签名
     * @param request
     * @param key
     * @return
     */
    public static void sign(Request request, String key){
        if(StringUtil.isEmpty(request)){
            return;
        }else if(StringUtil.isEmpty(request.getSign_type())){
            request.setSign("");
            return;
        }

        if(StringUtil.isEmpty(request.getRand_str())){
            request.setRand_str(RandomUtil.get32LenStr());
        }

        String signStr = getSortedString(request);

        if (SignType.RSA.getValue().equals(request.getSign_type())) {
            signStr = RSAUtil.sign(signStr, key);
        } else if (SignType.MD5.getValue().equals(request.getSign_type())) {
            signStr = genMD5Sign(signStr, key);
        } else {
            //抛出签名失败的异常
            throw new SDKException("签名失败，未预期的签名类型：" + request.getSign_type());
        }
        request.setSign(signStr);
    }

    protected static String getSortedString(Object obj){
        Field[] fields = obj.getClass().getDeclaredFields();

        Map<String, Object> signMap = new TreeMap<String, Object>();
        for(Field filed : fields){
            String name = filed.getName();
            if(NOT_SIGN_PARAM.contains(name)){//不参与签名或验签的参数直接跳过
                continue;
            }

            filed.setAccessible(true);
            try{
                signMap.put(name, filed.get(obj));
            }catch(Exception e){
                throw new RuntimeException(e);
            }
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
}
