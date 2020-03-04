package com.xpay.sdk.api.utils;

import com.xpay.sdk.api.entity.Request;
import com.xpay.sdk.api.entity.Response;
import com.xpay.sdk.api.entity.SecretKey;
import com.xpay.sdk.api.exceptions.SDKException;

/**
 * 请求处理工具类
 */
public class RequestUtil {

    /**
     * 发起交易请求
     *   说明：
     *      1、此方法不会对商户私钥做任何处理，理论上不会造成私钥泄漏，如果商户觉得把私钥传递到此方法不安全，可以在调用本方法前
     *          自行调用SignUtil的相关方法为请求参数生成签名，自行调用RSAUtil的相关方法对sec_key进行加解密。
     *      2、发起请求时，如果SecretKey.reqSignKey不为空，当前方法会自动为请求参数生成签名、得到系统响应之后也会自动对响应
     *          数据进行验签，签名或验签失败会抛出 SDKException 异常
     *      3、发送请求时，如果Request中的sec_key和SecretKey.secKeyEncryptKey都不为空，会自动给sec_key加密
     *      4、得到响应时，如果Response中的sec_key和SecretKey.secKeyDecryptKey都不为空，会自动给sec_key解密
     *
     * @param url
     * @param request
     * @param secretKey
     * @return
     * @throws SDKException
     */
    public static Response doRequest(String url, Request request, SecretKey secretKey) throws SDKException {
        //1.参数简单校验
        requestParamValid(request, secretKey);

        //2.如果data不是字符串，则转为字符串
        if(! (request.getData() instanceof String)){
            request.setData(JsonUtil.toString(request.getData()));
        }

        //3.对请求参数进行签名
        if(StringUtil.isNotEmpty(secretKey.getReqSignKey())){
            try{
                SignUtil.sign(request, secretKey.getReqSignKey());
            }catch(Exception e){
                throw new SDKException("签名失败: " + e.getMessage(), e);
            }
        }else if(StringUtil.isEmpty(request.getSign())){
            throw new SDKException("请为请求参数生成签名！");
        }

        //4.对sec_key执行rsa加密
        if(StringUtil.isNotEmpty(request.getSec_key()) && StringUtil.isNotEmpty(secretKey.getSecKeyEncryptKey())){
            String secKey = RSAUtil.encryptByPublicKey(request.getSec_key(), secretKey.getSecKeyEncryptKey());
            request.setSec_key(secKey);
        }

        //5.发起http(s)请求
        String respJson = null;
        try{
            respJson = OkHttpUtil.postJsonSync(url, JsonUtil.toString(request));
        }catch(Exception e){
            throw new SDKException("发送http请求时发生异常: " + e.getMessage(), e);
        }
        if(StringUtil.isEmpty(respJson)){
            throw new SDKException("请求完成，但响应信息为空");
        }

        //6.对响应数据进行对象转换
        Response response = null;
        try{
            response = JsonUtil.toBeanOrderly(respJson, Response.class);
        }catch (Exception e){
            throw new SDKException("请求完成，但响应信息转换失败: "+e.getMessage() + "，respJson=" + respJson, e);
        }

        //7.对响应数据进行验签
        boolean isSignOk = false;
        try{
            isSignOk = SignUtil.verify(response, secretKey.getRespVerifyKey());
        }catch (Exception e){
            throw new SDKException("请求完成，但响应信息验签失败: " + e.getMessage() + "，Response : " + JsonUtil.toString(response), e);
        }
        if(! isSignOk){
            throw new SDKException("响应数据验签不通过！Response : " + JsonUtil.toString(response));
        }

        //8.对响应数据中的sec_key进行rsa解密
        if(StringUtil.isNotEmpty(response.getSec_key()) && StringUtil.isNotEmpty(secretKey.getSecKeyDecryptKey())){
            String secKey = RSAUtil.decryptByPrivateKey(response.getSec_key(), secretKey.getSecKeyDecryptKey());
            response.setSec_key(secKey);
        }

        return response;
    }

    private static <T> void requestParamValid(Request request, SecretKey secretKey){
        if(StringUtil.isEmpty(request)){
            throw new SDKException("request不能为空");
        }else if(StringUtil.isEmpty(request.getMethod())){
            throw new SDKException("Request.method不能为空");
        }else if(StringUtil.isEmpty(request.getVersion())){
            throw new SDKException("Request.version不能为空");
        }else if(StringUtil.isEmpty(request.getData())){
            throw new SDKException("Request.data不能为空");
        }else if(StringUtil.isEmpty(request.getSign_type())){
            throw new SDKException("Request.sign_type不能为空");
        }else if(StringUtil.isEmpty(request.getMch_no())){
            throw new SDKException("Request.mch_no不能为空");
        }

        if(StringUtil.isEmpty(secretKey)){
            throw new SDKException("secretKey不能为空");
        }else if(StringUtil.isEmpty(secretKey.getRespVerifyKey())){
            throw new SDKException("secretKey.respVerifyKey不能为空");
        }
    }

}
