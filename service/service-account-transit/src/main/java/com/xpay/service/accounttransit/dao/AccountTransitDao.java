/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accounttransit.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import org.springframework.stereotype.Repository;

@Repository
public class AccountTransitDao extends MyBatisDao<AccountTransit, Long> {

    public AccountTransit getByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            return null;
        }
        return getOne("getByMerchantNo", merchantNo);
    }
}
