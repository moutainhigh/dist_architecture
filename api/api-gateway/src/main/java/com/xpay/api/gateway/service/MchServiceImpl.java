package com.xpay.api.gateway.service;

import com.google.common.cache.Cache;
import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.MerchantInfo;
import com.xpay.api.base.service.MchService;
import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.enums.common.SignTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

/**
 * @description 获取商户信息的实现类，为提高性能，建议本地加入缓存
 * @author: chenyf
 * @Date: 2019-02-24
 */
public class MchServiceImpl implements MchService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Cache<String, MerchantInfo> cache;//使用本地缓存，避免网络传输的开销

    public MchServiceImpl(@Nullable Cache<String, MerchantInfo> cache){
        this.cache = cache;
    }

    /**
     * 根据商户编号获取商户信息
     * @param mchNo
     * @return
     */
    @Override
    public MerchantInfo getMerchantInfo(String mchNo, APIParam param){
        String key = getCacheKey(mchNo, param.getSignType());
        MerchantInfo merchantInfo = this.getFromCache(key);
        if(merchantInfo == null){
            merchantInfo = this.getFromBizService(mchNo, param);
            this.storeToCache(key, merchantInfo);
        }
        return merchantInfo;
    }

    private String getCacheKey(String mchNo, String signType){
        return mchNo + "_" + signType;
    }

    private MerchantInfo getFromCache(String key){
        if (cache != null) {
            return cache.getIfPresent(key);
        }
        return null;
    }

    private void storeToCache(String key, MerchantInfo merchantInfo) {
        if (cache != null && merchantInfo != null) {
            cache.put(key, merchantInfo);
        }
    }

    /**
     * 从业务服务获取商户信息，可通过 http、dubbo 等等的方式来查询到商户信息，请根据自身的业务技术架构情况来决定
     * @param mchNo
     * @param param
     * @return
     */
    private MerchantInfo getFromBizService(String mchNo, APIParam param){
        MerchantInfo merchantInfo = new MerchantInfo();

        String md5Key = "B87F24EC25310CD4402A360CD150BF6B";
        String platPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKbGElB0faaAnSojqjfb3w7vT4wwOcCppHvn17X6ohhgoh3vvJLQMf7fZawDJPRLtCoHZp56uZBNFh6nze1rF+ljrT8UoRcky3O7CBMrocycqP/Lpja2y+pgctHClymlq0lIjTG2tBCBOhs5tjQ5dSA2F9rsUmVPlAjRZY8at0fnAgMBAAECgYEAhcTENeJqUp5A8ebvhqSGsz0Cykh4Wm/37ibVYDMrx2/jOS3tTLlQEMZxj9ppzsXWOgv7pMx9gSBDyM0CIRhQcWdvqsv8InpDXX7sz+JqYQB2CbjBrLX9CDhHpD+RQA/r2G9DAoLhZKoOjNPJh5WNrGtiH+DVnpEWF5SXnjfSzAECQQDlM+gW+7QdukDbBk+FlAh0zExnS8rzhpEa5+5EWiysWnHwjw2GOhqfFk7trScniaBbv4fv9WBj7OpJ5SHcUM4BAkEAukWmFLXojPfJ0siNflBBBI36CXBGbmIzynHg9a/CvOY51+QHl+kIQC7RwS5N94X9YtBN8hDYkjKpCG7syodl5wJAAPuU/iw8HHiE+KtxQdhdpOqPVU4M47hq/NuLuP1N/bsxi9+BJlcvcAkvc3NvnIrJhjsvAQdjT2pfost5trEeAQJAIpn5hfNcpYMJ/JvAnOwvh7cP8Vzn2G1pjXul/D2QASMLL61uM6vYGoQX9rixRv+e2BI1yHeUo2PBvo1Mczq/lQJBAIPq5QPJxvRZwfhIc7l+nzfBuhhoDBj09SkGmBpf6m/pJa/tOqgMP57OLLclU8qyZiEsXkpMpAoHWGdjFZairbo=";
        String mchPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCt8f64aJf4fHGXcHZZi/6MG7BpxqfNcVo8zV6lj4+/THj0usYORKvfzjBYc9fX/P+1DWmJ0jONBg11OVwGcCmoSHNWR4lDhf/JI3QnYm3rgKITsv598fSBPiZDL3uSbeoCuyJlAfrZcRiv4b7NGUt6Fr/wuRStZuEHwC9sNwAaiwIDAQAB";


        merchantInfo.setMchNo(mchNo);
        merchantInfo.setMchName("xx商户");
        merchantInfo.setMchStatus(PublicStatus.ACTIVE);
        merchantInfo.setSignType(param.getSignType());

        //默认使用这个共享密钥作为签名、验签、加密、解密
        if (String.valueOf(SignTypeEnum.MD5.getValue()).equals(param.getSignType())) {
            merchantInfo.setSignValidKey(md5Key);
            merchantInfo.setSignGenKey(md5Key);
            merchantInfo.setSecKeyDecryptKey(md5Key);
            merchantInfo.setSecKeyEncryptKey(md5Key);
        } else if (String.valueOf(SignTypeEnum.RSA.getValue()).equals(param.getSignType())) {
            merchantInfo.setSignValidKey(mchPublicKey);//验签用商户公钥
            merchantInfo.setSignGenKey(platPrivateKey);//签名用系统密钥
            merchantInfo.setSecKeyDecryptKey(platPrivateKey);//解密用系统密钥
            merchantInfo.setSecKeyEncryptKey(mchPublicKey);//加密用商户公钥
        } else {
            throw new BizException("未支持的签名类型: " + param.getSignType());
        }

        return merchantInfo;
    }
}
