package com.xpay.facade.accountsub.service;

import com.xpay.common.statics.enums.account.AccountStatusEnum;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accountsub.entity.AccountSub;

import java.util.List;
import java.util.Map;

/**
 * Author: chenyf
 * Date: 2019.12.19
 * Time: 17:06
 * Description: 子商户账户管理
 */
public interface AccountSubManageFacade {

    /**
     * 创建子商户账户
     *
     * @param merchantNo       商户编号.
     * @param merchantType     商户类型merchantTypeEnum.
     * @param parentMerchantNo 父商户编号.
     * @throws BizException 创建子商户账户失败时抛出异常
     */
    void createAccount(String merchantNo, Integer merchantType, String parentMerchantNo) throws BizException;

    /**
     * 子商户账户状态变更操作.
     *
     * @param merchantNo    子商户商户编号.
     * @param accountStatus 账户状态 {@link AccountStatusEnum}
     * @param desc          变更操作说明.
     * @throws BizException .
     */
    void changeAccountStatus(String merchantNo, int accountStatus, String desc) throws BizException;


    /**
     * 查询子商户帐户信息
     *
     * @param paramMap  查询参数
     * @param pageParam 分页参数
     * @throws BizException .
     */
    PageResult<List<AccountSub>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) throws BizException;


    /***
     * 根据用户编号获取主账户信息
     * @param merchantNo 商户编号.
     * @return 查询到的账户信息.
     * @throws BizException .
     */
    AccountSub getAccountByMerchantNo(String merchantNo) throws BizException;

}
