package com.xpay.service.accounttransit.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accounttransit.entity.AccountTransit;
import com.xpay.facade.accounttransit.service.AccountTransitManageFacade;
import com.xpay.service.accounttransit.biz.AccountTransitManageBiz;
import com.xpay.service.accounttransit.biz.AccountTransitQueryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * Author: chenyf
 * Date: 2019.12.19
 * Time: 16:04
 * Description: 在途账户管理
 */
@Service
public class AccountTransitManageFacadeImpl implements AccountTransitManageFacade {
    @Autowired
    private AccountTransitManageBiz accountTransitManageBiz;
    @Autowired
    private AccountTransitQueryBiz accountTransitQueryBiz;

    /**
     * 创建在途账户
     *
     * @param merchantNo   商户编号.
     * @param merchantType 商户类型 {@link com.xpay.common.statics.enums.merchant.MerchantTypeEnum}
     */
    @Override
    public void createAccount(String merchantNo, Integer merchantType) {
        accountTransitManageBiz.createAccount(merchantNo, merchantType);
    }

    /**
     * 账户状态变更操作.
     *
     * @param merchantNo    商户编号.
     * @param accountStatus 账户状态.
     * @param desc          变更操作说明.
     */
    public void changeAccountStatus(String merchantNo, int accountStatus, String desc) {
        accountTransitManageBiz.changeAccountStatus(merchantNo, accountStatus, desc);
    }

    @Override
    public PageResult<List<AccountTransit>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) {
        return accountTransitQueryBiz.listAccountPage(paramMap, pageParam);
    }

    @Override
    public AccountTransit getAccountByMerchantNo(String merchantNo) {
        return accountTransitQueryBiz.getAccountByMerchantNo(merchantNo);
    }


}
