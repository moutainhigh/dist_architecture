package com.xpay.facade.merchant.service;

import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.merchant.entity.Merchant;
import com.xpay.facade.merchant.entity.MerchantDetail;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 15:57
 * Description:商户管理服务
 */
public interface MerchantFacade {

    PageResult<List<Merchant>> listMerchantPage(Map<String, Object> paramMap, PageParam pageParam);

    void createMerchant(Merchant merchant, MerchantDetail merchantDetail) throws BizException;

    Merchant getMerchantByMerchantNo(String merchantNo);

    MerchantDetail getDetailByMerchantNo(String merchantNo);

}
