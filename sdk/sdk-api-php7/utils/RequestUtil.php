<?php
namespace xpay;

use Exception;

/**
 * 发起交易请求的工具类
 * Class RequestUtil
 * @package utils
 */
class RequestUtil{

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
     * @param string $url
     * @param Request $request
     * @param SecretKey $secretKey
     * @return Response
     * @throws SDKException
     * @throws \ReflectionException
     */
    public static function doRequest(string $url, Request $request, SecretKey $secretKey): Response
    {
        //1.参数校验
        self::requestParamValid($request, $secretKey);

        //2.data参数转换
        if(! is_string($request->getData())){
            //注意：如果data是一个没有实现JsonSerializable接口的对象，那这个类中被private修饰的属性将不能被json_encode()获取到
            $request->setData(json_encode($request->getData(), JSON_UNESCAPED_UNICODE));
        }

        //3.添加签名
        if($secretKey->getReqSignKey()){
            $signStr = SignUtil::getSortedString($request, false);
            $signStr = SignUtil::sign($signStr, $request->getSignType(), $secretKey->getReqSignKey());
            $request->setSign($signStr);
        }else if(! $request->getSign()){
            throw new SDKException(SDKException::BIZ_ERROR, "请对请求参数添加签名！");
        }

        //4.如果存在sec_key，则对其进行rsa加密
        if($request->getSecKey() && $secretKey->getSecKeyEncryptKey()){
            $request->setSecKey(RSAUtil::encrypt($request->getSecKey(), $secretKey->getSecKeyEncryptKey()));
        }

        //5.发起http请求
        $respJson = HttpUtil::postJsonSync($url, json_encode($request));
        if(! $respJson){
            throw new SDKException(SDKException::BIZ_ERROR, "请求完成，但响应信息为空");
        }

        //6.响应结果转换
        $response = new Response();
        try{
            ObjectUtil::fillProperties($response, $respJson);
        }catch (Exception $e){
            throw new SDKException(SDKException::BIZ_ERROR, "请求完成，但响应信息转换失败: " . $e->getMessage() . "，respJson = " . $respJson);
        }

        //7.验签
        $isOk = false;
        try{
            $signStr = SignUtil::getSortedString($response, false);
            $isOk = SignUtil::verify($signStr, $response->getSign(), $response->getSignType(), $secretKey->getRespVerifyKey());
        }catch (Exception $e){
            throw new SDKException(SDKException::BIZ_ERROR, "请求完成，但响应信息验签异常: " . $e->getMessage() . "，respJson = " . $respJson);
        }
        if($isOk !== true){
            throw new SDKException(SDKException::BIZ_ERROR, "响应信息验签不通过！ respJson = " . $respJson);
        }

        //8.对sec_key进行解密
        if($response->getSecKey() && $secretKey->getSecKeyDecryptKey()){
            $secKey = RSAUtil::decrypt($response->getSecKey(), $secretKey->getSecKeyDecryptKey());
            $response->setSecKey($secKey);
        }

        return $response;
    }

    /**
     * 简单参数校验
     * @param Request $request
     * @param SecretKey $secretKey
     * @throws Exception
     */
    private static function requestParamValid(Request $request, SecretKey $secretKey){
        if(! $request){
            throw new SDKException(SDKException::PARAM_ERROR, "request不能为空");
        }else if(! $request->getMethod()){
            throw new SDKException(SDKException::PARAM_ERROR, "Request.method不能为空");
        }else if(! $request->getVersion()){
            throw new SDKException(SDKException::PARAM_ERROR, "Request.version不能为空");
        }else if(! $request->getData()){
            throw new SDKException(SDKException::PARAM_ERROR, "Request.data不能为空");
        }else if(! $request->getSignType()){
            throw new SDKException(SDKException::PARAM_ERROR, "Request.sign_type不能为空");
        }else if(! $request->getMchNo()){
            throw new SDKException(SDKException::PARAM_ERROR, "Request.mch_no不能为空");
        }

        if(! $secretKey){
            throw new SDKException(SDKException::PARAM_ERROR, "secretKey不能为空");
        }else if(! $secretKey->getRespVerifyKey()){
            throw new SDKException(SDKException::PARAM_ERROR, "SecretKey.respVerifyKey不能为空");
        }
    }
}

