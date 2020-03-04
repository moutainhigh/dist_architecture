package com.xpay.service.accountsub.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountsub.entity.AccountSub;
import com.xpay.facade.accountsub.service.AccountSubManageFacade;
import com.xpay.service.accountsub.biz.AccountSubManageBiz;
import com.xpay.service.accountsub.biz.AccountSubQueryBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * Author: chenyf
 * Date: 2019.12.19
 * Time: 16:04
 * Description: 子商户账户管理
 */
@Service
public class AccountSubManageFacadeImpl implements AccountSubManageFacade {
    @Autowired
    private AccountSubManageBiz accountSubManageBiz;
    @Autowired
    private AccountSubQueryBiz accountSubQueryBiz;

    /**
     * 创建子商户账户
     *
     * @param merchantNo       商户编号.
     * @param merchantType     商户类型 {@link com.xpay.common.statics.enums.merchant.MerchantTypeEnum}
     * @param parentMerchantNo 父商户编号.
     */
    @Override
    public void createAccount(String merchantNo, Integer merchantType, String parentMerchantNo) {
        accountSubManageBiz.createAccount(merchantNo, merchantType, parentMerchantNo);
    }

    /**
     * 账户状态变更操作.
     *
     * @param merchantNo    商户编号.
     * @param accountStatus 账户状态.
     * @param desc          变更操作说明.
     */
    public void changeAccountStatus(String merchantNo, int accountStatus, String desc) {
        accountSubManageBiz.changeAccountStatus(merchantNo, accountStatus, desc);
    }

    @Override
    public PageResult<List<AccountSub>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) {
        return accountSubQueryBiz.listAccountPage(paramMap, pageParam);
    }

    @Override
    public AccountSub getAccountByMerchantNo(String merchantNo) {
        return accountSubQueryBiz.getAccountByMerchantNo(merchantNo);
    }


}
