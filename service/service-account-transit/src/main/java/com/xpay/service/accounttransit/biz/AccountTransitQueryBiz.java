package com.xpay.service.accounttransit.biz;

import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetail;
import com.xpay.facade.accounttransit.entity.AccountTransitProcessDetailHistory;
import com.xpay.service.accounttransit.dao.AccountTransitDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessDetailDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessDetailHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2020.2.10
 * Time: 12:24
 * Description: 在途账户信息查询biz
 */
@Component
public class AccountTransitQueryBiz {
    @Autowired
    private AccountTransitDao accountTransitDao;

    @Autowired
    private AccountTransitProcessDetailDao accountTransitProcessDetailDao;

    @Autowired
    private AccountTransitProcessDetailHistoryDao accountTransitProcessDetailHistoryDao;

    /**
     * 分页查询在途账户信息
     *
     * @param paramMap  .
     * @param pageParam .
     * @return .
     */
    public PageResult<List<AccountTransit>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) {
        return accountTransitDao.listPage(paramMap, pageParam);
    }

    /**
     * 根据商户编号获取在途账户信息
     *
     * @param merchantNo 商户编号.
     * @return account 查询到的账户信息.
     */
    public AccountTransit getAccountByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            return null;
        }
        return accountTransitDao.getByMerchantNo(merchantNo);
    }

    /**
     * 分页查询账务处理明细信息
     *
     * @param paramMap  .
     * @param pageParam .
     * @param inHistory 是否用历史表查询
     * @return .
     */
    public PageResult<List<AccountTransitProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountTransitProcessDetailDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountTransitProcessDetailHistoryDao.listPage(paramMap, pageParam);
            List<AccountTransitProcessDetail> collect = ((List<AccountTransitProcessDetailHistory>) pageResult.getData()).stream()
                    .map(AccountTransitProcessDetail::newFromHistory).collect(Collectors.toList());
            pageResult.setData(collect);
            return pageResult;
        }
    }

    /**
     * 查询账务处理明细
     *
     * @param id        .
     * @param inHistory .
     * @return .
     */
    public AccountTransitProcessDetail getProcessDetailById(Long id, boolean inHistory) {
        if (!inHistory) {
            return accountTransitProcessDetailDao.getById(id);
        } else {
            return AccountTransitProcessDetail.newFromHistory(accountTransitProcessDetailHistoryDao.getById(id));
        }
    }


}
