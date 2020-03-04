package com.xpay.service.merchant.facade;

import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.merchant.entity.Merchant;
import com.xpay.facade.merchant.entity.MerchantDetail;
import com.xpay.facade.merchant.service.MerchantFacade;
import com.xpay.service.merchant.biz.MerchantBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 15:57
 * Description:商户管理服务
 */
@Service
public class MerchantFacadeImpl implements MerchantFacade {
    @Autowired
    private MerchantBiz merchantBiz;

    public PageResult<List<Merchant>> listMerchantPage(Map<String, Object> paramMap, PageParam pageParam) throws BizException {
        return merchantBiz.listMerchantPage(paramMap, pageParam);
    }

    public void createMerchant(Merchant merchant, MerchantDetail merchantDetail) throws BizException {
        merchantBiz.createMerchant(merchant, merchantDetail);
    }

    public Merchant getMerchantByMerchantNo(String merchantNo) throws BizException {
        return merchantBiz.getMerchantByMerchantNo(merchantNo);
    }

    public MerchantDetail getDetailByMerchantNo(String merchantNo) throws BizException {
        return merchantBiz.getDetailByMerchantNo(merchantNo);
    }

}
