package com.xpay.api.gateway.helper;

import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import com.xpay.api.base.service.MchService;
import com.xpay.api.base.utils.SignUtil;
import com.xpay.api.base.params.MerchantInfo;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.RSAUtil;
import com.xpay.common.util.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author chenyf
 * @description 签名、验签的组件，需要使用此组件的项目，需要自己配置这个@Bean，同时自己实现UserService、ValidFailService 然后再通过Spring进行依赖注入
 * @date 2019-02-20
 */
public final class RequestHelper {
    private Logger logger = LoggerFactory.getLogger(RequestHelper.class);
    private MchService mchService;

    public RequestHelper(MchService mchService) {
        this.mchService = mchService;
    }

    /**
     * IP校验
     *
     * @param ip
     * @param ipValidKey
     * @param requestParam
     * @return
     */
    public boolean ipVerify(String ip, String ipValidKey, RequestParam requestParam, APIParam param) {
        if (requestParam == null || StringUtil.isEmpty(requestParam.getMch_no())) {
            return false;
        }

        MerchantInfo merchantInfo = mchService.getMerchantInfo(requestParam.getMch_no(), param);
        Map<String, String> ipMap = merchantInfo.getIpValidMap();
        String expectIp = ipMap == null ? null : ipMap.get(ipValidKey);//预期的Ip
        logger.debug("==>gateway ipVerify mchIp:{},expectIp:{},mchNo:{}", ip, expectIp, requestParam.getMch_no());
        //为空说明不需要检验IP
        if (StringUtil.isEmpty(expectIp)) {
            return true;
        } else {
            return expectIp.contains(ip);
        }
    }

    /**
     * 签名校验
     *
     * @param requestParam
     * @return
     */
    public boolean signVerify(RequestParam requestParam, APIParam param) {
        if (requestParam == null || StringUtil.isEmpty(requestParam.getMch_no())) {
            return false;
        }

        MerchantInfo merchantInfo = mchService.getMerchantInfo(requestParam.getMch_no(), param);

        try {
            return SignUtil.verify(requestParam, merchantInfo);
        } catch (Throwable e) {
            logger.error("验签失败，因为验签时出现异常 RequestParam = {} e:", JsonUtil.toString(requestParam), e);
            return false;
        }
    }

    /**
     * 商户信息校验
     * @param requestParam
     * @param param
     * @return
     */
    public boolean mchVerify(RequestParam requestParam, APIParam param) {
        if (requestParam == null || StringUtil.isEmpty(requestParam.getMch_no())) {
            return false;
        }

        MerchantInfo merchantInfo = mchService.getMerchantInfo(requestParam.getMch_no(), param);

        //判断商户的激活状态
        if (PublicStatus.ACTIVE == merchantInfo.getMchStatus()) {
            return true;
        }else{
            throw GatewayException.fail(ApiRespCodeEnum.MCH_FAIL.getCode(), "状态受限", GatewayErrorCode.PARAM_CHECK_ERROR);
        }
    }


    /**
     * 给aes_key解密
     *
     * @param requestParam
     */
    public void secKeyDecrypt(RequestParam requestParam, APIParam param) {
        if (requestParam == null || StringUtil.isEmpty(requestParam.getMch_no()) || StringUtil.isEmpty(requestParam.getSec_key())) {
            return;
        }

        MerchantInfo merchantInfo = mchService.getMerchantInfo(requestParam.getMch_no(), param);
        if (merchantInfo == null || StringUtil.isEmpty(merchantInfo.getSecKeyDecryptKey())) {
            return;
        }
        //使用平台敏感信息key解密RSA私钥解密
        String secKey = RSAUtil.decryptByPrivateKey(requestParam.getSec_key(), merchantInfo.getSecKeyDecryptKey());
        requestParam.setSec_key(secKey);
    }

    /**
     * 生成签名,如果有secKey，使用商户提供的公钥对其进行加密
     *
     * @param responseParam
     */
    public void signAndEncrypt(ResponseParam responseParam, APIParam param) {
        if (responseParam == null) {
            responseParam = new ResponseParam();
            responseParam.setSign("");
            return;
        }
        if (StringUtil.isEmpty(responseParam.getMch_no())) {
            responseParam.setSign("");
            return;
        }

        MerchantInfo merchantInfo = mchService.getMerchantInfo(responseParam.getMch_no(), param);
        if (merchantInfo == null) {
            responseParam.setSign("");
            return;
        }
        if(StringUtil.isEmpty(responseParam.getSign_type())){
            responseParam.setSign_type(merchantInfo.getSignType());
        }

        //如果加密失败，定会抛出异常，此时签名串就为空值，客户端就会验签失败，所以，加密这一步骤要放在加签名之前
        if (StringUtil.isNotEmpty(responseParam.getSec_key()) && StringUtil.isNotEmpty(merchantInfo.getSecKeyEncryptKey())) {
            try {
                responseParam.setSec_key(RSAUtil.encryptByPublicKey(responseParam.getSec_key(), merchantInfo.getSecKeyEncryptKey()));
            } catch (Exception e) {
                logger.error("secKey加密错误，responseParam:{}, e:", JsonUtil.toString(responseParam), e);
            }
        }

        SignUtil.sign(responseParam, merchantInfo);
    }

    public class Result<T>{
        private boolean isVerifyOk = false;
        private T otherInfo;

        public boolean isVerifyOk() {
            return isVerifyOk;
        }

        public void setVerifyOk(boolean verifyOk) {
            isVerifyOk = verifyOk;
        }

        public T getOtherInfo() {
            return otherInfo;
        }

        public void setOtherInfo(T otherInfo) {
            this.otherInfo = otherInfo;
        }
    }
}
