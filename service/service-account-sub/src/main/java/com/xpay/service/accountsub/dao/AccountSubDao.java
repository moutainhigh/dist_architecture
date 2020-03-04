/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accountsub.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountsub.entity.AccountSub;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AccountSubDao extends MyBatisDao<AccountSub, Long> {

    public AccountSub getByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            return null;
        }
        return getOne("getByMerchantNo", merchantNo);
    }

    //todo 各个账户都要改
    public List<String> listMerchantNoPage(Map<String, Object> paramMap, Integer pageCurrent, Integer pageSize, String sortColumn) {
        int calPageCurrent = pageCurrent < 1 ? 0 : (pageCurrent - 1);
        Integer offset = calPageCurrent * pageSize;
        if (paramMap == null) {
            paramMap = new HashMap();
        }
        paramMap.put("offset", offset);
        paramMap.put("pageSize", pageSize);
        return listBy("listUserNoPage", paramMap);
    }
}
