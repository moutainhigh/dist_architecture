package com.xpay.api.base.utils;

import com.xpay.api.base.dto.CallbackDto;
import com.xpay.api.base.dto.CallbackResp;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.*;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步回调商户的工具类
 */
public class CallbackUtil {
    private static Logger logger = LoggerFactory.getLogger(CallbackUtil.class);

    /**
     * 回调商户
     *
     * @param url              请求的url
     * @param callback         回调参数对象
     * @param signPriKey       生成签名的rsa密钥
     * @param secKeyEncryptKey 对sec_key进行加密的rsa公钥
     * @return
     */
    public static CallbackResp callbackSync(String url, CallbackDto callback, String signPriKey, String secKeyEncryptKey) {
        //1.如果不是string就先转成string，同时，把参数名转成下划线分割
        if (!(callback.getData() instanceof String)) {
            callback.setData(JsonUtil.toStringUnderline(callback.getData()));
        }
        if (StringUtil.isEmpty(callback.getRand_str())) {
            callback.setRand_str(RandomUtil.get32LenStr());
        }

        //2.对请求参数进行签名
        try {
            String signStr = SignUtil.sign(callback, callback.getSign_type(), signPriKey);
            callback.setSign(signStr);
        } catch (Exception e) {
            throw new BizException("签名失败: " + e.getMessage(), e);
        }

        //3.对sec_key执行rsa加密
        if (StringUtil.isNotEmpty(signPriKey) && StringUtil.isNotEmpty(secKeyEncryptKey)) {
            String secKey = RSAUtil.encryptByPublicKey(callback.getSec_key(), secKeyEncryptKey);
            callback.setSec_key(secKey);
        }

        //4.发起http(s)请求，然后封装响应数据
        CallbackResp resp;
        Response response = null;
        Exception httpEx = null;
        try {
            response = OkHttpUtil.postJsonSync(url, JsonUtil.toString(callback));
        } catch (Exception ex) {
            httpEx = ex;
        }
        if (response == null || response.code() != 200) {
            logger.error("发送http请求时发生异常,responseCode={}", response == null ? null : response.code(), httpEx);
            resp = new CallbackResp();
            resp.setStatus(CallbackResp.RETRY);
            resp.setErrMsg("发送http请求时发生异常");
            return resp;
        }

        String respJson = null;
        try {
            resp = JsonUtil.toBean(respJson = response.body().string(), CallbackResp.class);
        } catch (Exception ex) {
            logger.error("返回数据解析异常,respJson={}", respJson, ex);
            resp = new CallbackResp();
            resp.setStatus(CallbackResp.FAIL);
            resp.setErrMsg("返回数据解析异常: " + respJson);
        }
        return resp;
    }
}
