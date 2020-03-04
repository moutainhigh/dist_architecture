package com.xpay.service.accountsub.biz;

import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountsub.entity.AccountSub;
import com.xpay.facade.accountsub.entity.AccountSubProcessDetail;
import com.xpay.facade.accountsub.entity.AccountSubProcessDetailHistory;
import com.xpay.service.accountsub.dao.AccountSubDao;
import com.xpay.service.accountsub.dao.AccountSubProcessDetailDao;
import com.xpay.service.accountsub.dao.AccountSubProcessDetailHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019.12.18
 * Time: 18:21
 * Description: 子商户账户信息查询biz
 */
@Component
public class AccountSubQueryBiz {
    @Autowired
    private AccountSubDao accountSubDao;

    @Autowired
    private AccountSubProcessDetailDao accountSubProcessDetailDao;

    @Autowired
    private AccountSubProcessDetailHistoryDao accountSubProcessDetailHistoryDao;

    /**
     * 多条件分页查询主账户的商户编号
     *
     * @param paramMap    .
     * @param pageCurrent .
     * @param pageSize    .
     * @param sortColumn  .
     * @return
     */
    public List<String> listMerchantNoPage(Map<String, Object> paramMap, Integer pageCurrent, Integer pageSize, String sortColumn) {
        return accountSubDao.listMerchantNoPage(paramMap, pageCurrent, pageSize, sortColumn);
    }

    /**
     * 分页查询子商户账户信息
     *
     * @param paramMap  .
     * @param pageParam .
     * @return .
     */
    public PageResult<List<AccountSub>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) {
        return accountSubDao.listPage(paramMap, pageParam);
    }

    /**
     * 根据商户编号获取子商户账户信息
     *
     * @param merchantNo 商户编号.
     * @return account 查询到的账户信息.
     */
    public AccountSub getAccountByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            return null;
        }
        return accountSubDao.getByMerchantNo(merchantNo);
    }

    /**
     * 根据多个商户编号获取账户信息.
     *
     * @param merchantNoList 商户编号列表.
     * @return List<Account> 查询到的账户信息.
     * @deprecated todo 需要删除
     */
    public List<AccountSub> listAccountByMerchantNos(List<String> merchantNoList) {
        if (merchantNoList == null || merchantNoList.isEmpty()) {
            return null;
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("merchantNoList", merchantNoList);
        return accountSubDao.listBy(param);
    }

    /**
     * 分页查询账务处理明细信息
     *
     * @param paramMap  .
     * @param pageParam .
     * @param inHistory 是否用历史表查询
     * @return .
     */
    public PageResult<List<AccountSubProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountSubProcessDetailDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountSubProcessDetailHistoryDao.listPage(paramMap, pageParam);
            List<AccountSubProcessDetail> collect = ((List<AccountSubProcessDetailHistory>) pageResult.getData()).stream()
                    .map(AccountSubProcessDetail::newFromHistory).collect(Collectors.toList());
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
    public AccountSubProcessDetail getProcessDetailById(Long id, boolean inHistory) {
        if (!inHistory) {
            return accountSubProcessDetailDao.getById(id);
        } else {
            return AccountSubProcessDetail.newFromHistory(accountSubProcessDetailHistoryDao.getById(id));
        }
    }


}
