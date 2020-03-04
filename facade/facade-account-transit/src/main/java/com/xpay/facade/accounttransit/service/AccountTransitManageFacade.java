package com.xpay.facade.accounttransit.service;

import com.xpay.common.statics.enums.account.AccountStatusEnum;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.accounttransit.entity.AccountTransit;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.2.10
 * Time: 11:47
 * Description: 在途账户管理
 */
public interface AccountTransitManageFacade {

    /**
     * 创建在途账户
     *
     * @param merchantNo   商户编号.
     * @param merchantType 商户类型merchantTypeEnum.
     * @throws BizException 创建在途账户失败时抛出异常
     */
    void createAccount(String merchantNo, Integer merchantType) throws BizException;

    /**
     * 在途账户状态变更操作.
     *
     * @param merchantNo    商户编号.
     * @param accountStatus 账户状态 {@link AccountStatusEnum}
     * @param desc          变更操作说明.
     * @throws BizException .
     */
    void changeAccountStatus(String merchantNo, int accountStatus, String desc) throws BizException;


    /**
     * 查询在途账户信息
     *
     * @param paramMap  查询参数
     * @param pageParam 分页参数
     * @throws BizException
     */
    PageResult<List<AccountTransit>> listAccountPage(Map<String, Object> paramMap, PageParam pageParam) throws BizException;


    /***AccountTransitBizException
     * 根据用户编号获取在途账户信息
     * @param merchantNo 商户编号.
     * @return 查询到的账户信息.
     * @throws BizException .
     */
    AccountTransit getAccountByMerchantNo(String merchantNo) throws BizException;

}
