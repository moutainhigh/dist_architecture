package com.xpay.service.accountmch.biz;

import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetail;
import com.xpay.facade.accountmch.entity.AccountMchProcessDetailHistory;
import com.xpay.service.accountmch.dao.AccountMchDao;
import com.xpay.service.accountmch.dao.AccountMchProcessDetailDao;
import com.xpay.service.accountmch.dao.AccountMchProcessDetailHistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luobinzhao
 * @date 2020/1/14 14:31
 */
@Component
public class AccountMchQueryBiz {
    @Autowired
    private AccountMchDao accountMchDao;

    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;

    @Autowired
    private AccountMchProcessDetailHistoryDao accountMchProcessDetailHistoryDao;

    /**
     * 多条件分页查询主账户的商户编号
     *
     * @param paramMap    .
     * @param pageCurrent .
     * @param pageSize    .
     * @return
     */
    public List<String> listMerchantNoPage(Map<String, Object> paramMap, Integer pageCurrent, Integer pageSize) {
        return accountMchDao.listMerchantNoPage(paramMap, pageCurrent, pageSize);
    }

    /**
     * 分页查询商户账户信息
     *
     * @param paramMap  .
     * @param pageParam .
     * @return .
     */
    public PageResult<List<AccountMch>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) {
        return accountMchDao.listPage(paramMap, pageParam);
    }

    /**
     * 根据商户编号获取商户账户信息
     *
     * @param merchantNo 商户编号.
     * @return account 查询到的账户信息.
     */
    public AccountMch getAccountByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            return null;
        }
        return accountMchDao.getByMerchantNo(merchantNo);
    }

    /**
     * 根据多个商户编号获取账户信息.
     *
     * @param merchantNoList 商户编号列表.
     * @return List<Account> 查询到的账户信息.
     * @deprecated
     */
    public List<AccountMch> listAccountByMerchantNos(List<String> merchantNoList) {
        if (merchantNoList == null || merchantNoList.isEmpty()) {
            return null;
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("merchantNoList", merchantNoList);
        return accountMchDao.listBy(param);
    }

    /**
     * 分页查询账务处理明细信息
     *
     * @param paramMap  .
     * @param pageParam .
     * @param inHistory 是否用历史表查询
     * @return .
     */
    public PageResult<List<AccountMchProcessDetail>> listProcessDetailPage(Map<String, Object> paramMap, PageParam pageParam, boolean inHistory) {
        if (!inHistory) {
            return accountMchProcessDetailDao.listPage(paramMap, pageParam);
        } else {
            if (paramMap == null || paramMap.get("createTimeBegin") == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("查询历史数据需要传入开始时间参数createTimeBegin");
            }
            PageResult pageResult = accountMchProcessDetailHistoryDao.listPage(paramMap, pageParam);
            List<AccountMchProcessDetail> collect = ((List<AccountMchProcessDetailHistory>) pageResult.getData()).stream()
                    .map(AccountMchProcessDetail::newFromHistory).collect(Collectors.toList());
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
    public AccountMchProcessDetail getProcessDetailById(Long id, boolean inHistory) {
        if (id == null) {
            return null;
        }
        if (!inHistory) {
            return accountMchProcessDetailDao.getById(id);
        } else {
            return AccountMchProcessDetail.newFromHistory(accountMchProcessDetailHistoryDao.getById(id));
        }
    }
}
