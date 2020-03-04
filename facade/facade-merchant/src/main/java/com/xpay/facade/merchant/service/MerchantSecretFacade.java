package com.xpay.facade.merchant.service;

import com.xpay.common.statics.exception.BizException;
import com.xpay.facade.merchant.entity.MerchantSecret;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 17:53
 * Description:商户密钥管理接口
 */
public interface MerchantSecretFacade {
    /**
     * 创建商户新密钥
     *
     * @param merchantNo        商户编号
     * @param merchantPublicKey 公钥字符串
     * @param signType          签名类型 {@link com.xpay.common.statics.enums.common.SignTypeEnum}
     */
    void create(String merchantNo, String merchantPublicKey, Integer signType) throws BizException;


    /**
     * 根据商户号获取新密钥
     *
     * @param merchantNo 商户编号
     * @return 商户新密钥
     */
    MerchantSecret getByMerchantNo(String merchantNo) throws BizException;

    /**
     * 更新商户公钥
     *
     * @param merchantNo        商户编号
     * @param merchantPublicKey 商户公钥
     */
    void updateMerchantPublicKey(String merchantNo, String merchantPublicKey) throws BizException;

    /**
     * 更新平台公私钥
     *
     * @param merchantNo 商户编号
     */
    void updatePlatformKey(String merchantNo) throws BizException;

}