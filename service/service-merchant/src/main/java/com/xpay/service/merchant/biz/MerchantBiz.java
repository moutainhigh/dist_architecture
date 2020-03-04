package com.xpay.service.merchant.biz;

import com.xpay.common.statics.enums.merchant.MerchantTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.merchant.entity.Merchant;
import com.xpay.facade.merchant.entity.MerchantDetail;
import com.xpay.service.merchant.dao.MerchantDao;
import com.xpay.service.merchant.dao.MerchantDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 16:07
 * Description:商户管理BIZ
 */
@Component
public class MerchantBiz {

    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private MerchantDetailDao merchantDetailDao;

    public PageResult<List<Merchant>> listMerchantPage(Map<String, Object> paramMap, PageParam pageParam) {
        return merchantDao.listPage(paramMap, pageParam);
    }

    /**
     * 创建商户信息
     *
     * @param merchant       商户基本信息
     * @param merchantDetail 商户详细信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void createMerchant(Merchant merchant, MerchantDetail merchantDetail) {
        if (merchant.getMerchantType() == MerchantTypeEnum.SUB_MERCHANT.getValue() && StringUtil.isEmpty(merchant.getParentMerchantNo())) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("创建子商户必须提供父商户编号");
        }
        merchantDao.insert(merchant);
        merchantDetailDao.insert(merchantDetail);
    }

    public Merchant getMerchantByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }
        return merchantDao.getByMerchantNo(merchantNo);
    }

    public MerchantDetail getDetailByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }
        return merchantDetailDao.getByMerchantNo(merchantNo);
    }

}
