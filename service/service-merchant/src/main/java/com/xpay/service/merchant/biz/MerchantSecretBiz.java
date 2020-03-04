package com.xpay.service.merchant.biz;

import com.xpay.common.statics.enums.common.SignTypeEnum;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.RSAUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.merchant.entity.MerchantSecret;
import com.xpay.service.merchant.dao.MerchantSecretDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 16:07
 * Description:商户密钥管理BIZ
 */
@Component
public class MerchantSecretBiz {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MerchantSecretDao merchantSecretDao;

    public void create(String merchantNo, String merchantPublicKey, Integer signType) throws BizException {
        logger.info("创建商户密钥,merchantNo={},merchantPublicKey={},signType={}", merchantNo, merchantPublicKey, signType);
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("商户号不能为空");
        } else if (SignTypeEnum.getEnum(signType) == null) {
            logger.error("不支持的签名方法,merchantNo={},merchantPublicKey={},signType={}", merchantNo, merchantPublicKey, signType);
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("不支持的签名方式");
        }

        MerchantSecret secret = merchantSecretDao.getByMerchantNo(merchantNo);
        if (secret != null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("商户密钥已存在");
        }

        MerchantSecret newSecret = new MerchantSecret();
        newSecret.setModifyTime(new Date());
        newSecret.setMerchantNo(merchantNo);
        newSecret.setSignType(signType);

        // 生成平台公私钥
        if (signType == SignTypeEnum.RSA.getValue()) {
            if (!RSAUtil.validPublicKey(merchantPublicKey)) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("merchantPublicKey无效");
            }
            newSecret.setMerchantPublicKey(merchantPublicKey);
            Map<String, String> keyMap = RSAUtil.genKeyPair();
            newSecret.setPlatformPrivateKey(keyMap.get(RSAUtil.PRIVATE_KEY));
            newSecret.setPlatformPublicKey(keyMap.get(RSAUtil.PUBLIC_KEY));
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("不支持的签名算法类型");
        }
        merchantSecretDao.insert(newSecret);
        logger.info("创建商户密钥成功,merchantNo={},merchantPublicKey={},signType={}", merchantNo, merchantPublicKey, signType);
    }

    public MerchantSecret getByMerchantNo(String merchantNo) throws BizException {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }
        return merchantSecretDao.getByMerchantNo(merchantNo);
    }

    public void updateMerchantPublicKey(String merchantNo, String merchantPublicKey) {
        logger.info("更新商户公钥,merchantNo={},merchantPublicKey={}", merchantNo, merchantPublicKey);
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }

        MerchantSecret secret = merchantSecretDao.getByMerchantNo(merchantNo);
        if (secret == null) {
            logger.error("商户密钥不存在,merchantNo={}", merchantNo);
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("商户密钥不存在");
        }

        if (secret.getSignType() == SignTypeEnum.RSA.getValue()) {
            if (!RSAUtil.validPublicKey(merchantPublicKey)) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("merchantPublicKey无效");
            }
            secret.setMerchantPublicKey(merchantPublicKey);
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("不支持的签名算法类型");
        }

        merchantSecretDao.update(secret);
        logger.info("更新商户公钥成功,merchantNo={},merchantPublicKey={}", merchantNo, merchantPublicKey);
    }

    public void updatePlatformKey(String merchantNo) {
        logger.info("更新平台密钥,merchantNo={}", merchantNo);
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }

        MerchantSecret secret = merchantSecretDao.getByMerchantNo(merchantNo);
        if (secret == null) {
            logger.error("商户密钥不存在,merchantNo={}", merchantNo);
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("商户密钥不存在");
        }

        // 生成平台公私钥
        if (secret.getSignType() == SignTypeEnum.RSA.getValue()) {
            Map<String, String> keyMap = RSAUtil.genKeyPair();
            secret.setPlatformPrivateKey(keyMap.get(RSAUtil.PRIVATE_KEY));
            secret.setPlatformPublicKey(keyMap.get(RSAUtil.PUBLIC_KEY));
        } else {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("不支持的签名算法类型");
        }

        merchantSecretDao.update(secret);
        logger.info("更新平台密钥成功,merchantNo={}", merchantNo);

    }

}
