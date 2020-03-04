package com.xpay.service.merchant.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.merchant.entity.MerchantDetail;
import org.springframework.stereotype.Repository;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 16:01
 * Description:
 */
@Repository
public class MerchantDetailDao extends MyBatisDao<MerchantDetail, Long> {

    public MerchantDetail getByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }
        return getOne("getByMerchantNo", merchantNo);
    }

}
