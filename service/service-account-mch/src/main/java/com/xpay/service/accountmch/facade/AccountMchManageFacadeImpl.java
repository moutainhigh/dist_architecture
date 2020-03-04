package com.xpay.service.accountmch.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.facade.accountmch.service.AccountMchManageFacade;
import com.xpay.service.accountmch.biz.AccountMchManageBiz;
import com.xpay.service.accountmch.biz.AccountMchQueryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author luobinzhao
 * @date 2020/1/14 16:30
 */
@Service
public class AccountMchManageFacadeImpl implements AccountMchManageFacade {
    @Autowired
    private AccountMchManageBiz accountMchManageBiz;
    @Autowired
    private AccountMchQueryBiz accountMchQueryBiz;

    /**
     * 创建商户账户
     *
     * @param merchantNo   商户编号.
     * @param merchantType 商户类型.
     */
    @Override
    public void createAccount(String merchantNo, int merchantType) {
        accountMchManageBiz.createAccount(merchantNo, merchantType);
    }

    /**
     * 账户状态变更操作.
     *
     * @param merchantNo    商户编号.
     * @param accountStatus 账户状态.
     * @param desc          变更操作说明.
     */
    public void changeAccountStatus(String merchantNo, int accountStatus, String desc) {
        accountMchManageBiz.changeAccountStatus(merchantNo, accountStatus, desc);
    }

    @Override
    public PageResult<List<AccountMch>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) {
        return accountMchQueryBiz.listAccountPage(paramMap, pageParam);
    }

    @Override
    public AccountMch getAccountByMerchantNo(String merchantNo) {
        return accountMchQueryBiz.getAccountByMerchantNo(merchantNo);
    }

}
