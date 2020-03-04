/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accountsub.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.dto.account.AccountDetailDto;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accountsub.entity.AccountSubProcessDetailHistory;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AccountSubProcessDetailHistoryDao extends MyBatisDao<AccountSubProcessDetailHistory, Long> {

    public List<AccountDetailDto> listDetailDtoByMchNoAndTrxNo(String merchantNo, String trxNo, Date createTimeBegin) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("merchantNo", merchantNo);
        paramMap.put("trxNo", trxNo);
        //todo 这里的createTimeBegin
        paramMap.put("createTimeBegin", DateUtil.formatDate(createTimeBegin));
        return listBy("listDetailDtoByMchNoAndTrxNo", paramMap);
    }
}
