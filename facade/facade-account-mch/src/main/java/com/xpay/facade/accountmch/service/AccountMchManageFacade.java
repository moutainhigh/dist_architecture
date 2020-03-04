package com.xpay.facade.accountmch.service;

import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountmch.entity.AccountMch;

import java.util.List;
import java.util.Map;

/**
 * @author luobinzhao
 * @date 2020/1/14 10:28
 */
public interface AccountMchManageFacade {
    /**
     * 创建主商户账户
     *
     * @param merchantNo   商户编号.
     * @param merchantType 商户类型.
     * @throws BizException 创建主商户账户失败时抛出异常
     */
    void createAccount(String merchantNo, int merchantType) throws BizException;

    /**
     * 主商户账户状态变更操作.
     *
     * @param merchantNo    主商户商户编号.
     * @param accountStatus 账户状态 {@link com.xpay.common.statics.enums.account.AccountStatusEnum}
     * @param desc          变更操作说明.
     * @throws BizException .
     */
    void changeAccountStatus(String merchantNo, int accountStatus, String desc) throws BizException;


    /**
     * 查询主商户帐户信息
     *
     * @param paramMap  查询参数
     * @param pageParam 分页参数
     * @throws BizException .
     */
    PageResult<List<AccountMch>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) throws BizException;


    /***
     * 根据用户编号获取主账户信息
     * @param merchantNo 商户编号.
     * @return 查询到的账户信息.
     * @throws BizException .
     */
    AccountMch getAccountByMerchantNo(String merchantNo) throws BizException;
}
