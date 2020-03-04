package com.xpay.service.merchant.facade;

import com.xpay.common.statics.exception.BizException;
import com.xpay.facade.merchant.entity.MerchantSecret;
import com.xpay.facade.merchant.service.MerchantSecretFacade;
import com.xpay.service.merchant.biz.MerchantSecretBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 15:57
 * Description:商户密钥管理服务
 */
@Service
public class MerchantSecretFacadeImpl implements MerchantSecretFacade {
    @Autowired
    private MerchantSecretBiz merchantSecretBiz;

    @Override
    public void create(String merchantNo, String merchantPublicKey, Integer signType) throws BizException {
        merchantSecretBiz.create(merchantNo, merchantPublicKey, signType);
    }

    @Override
    public MerchantSecret getByMerchantNo(String merchantNo) throws BizException {
        return merchantSecretBiz.getByMerchantNo(merchantNo);
    }

    @Override
    public void updateMerchantPublicKey(String merchantNo, String merchantPublicKey) throws BizException {
        merchantSecretBiz.updateMerchantPublicKey(merchantNo, merchantPublicKey);
    }

    @Override
    public void updatePlatformKey(String merchantNo) throws BizException {
        merchantSecretBiz.updatePlatformKey(merchantNo);
    }
}
