package com.xpay.service.accountmch.biz;

import com.xpay.common.statics.enums.account.AccountStatusEnum;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountmch.entity.AccountMch;
import com.xpay.service.accountmch.dao.AccountMchDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * @author luobinzhao
 * @date 2020/1/14 14:30
 */
@Component
public class AccountMchManageBiz {
    private Logger logger = LoggerFactory.getLogger(AccountMchManageBiz.class);

    @Autowired
    private AccountMchDao accountMchDao;

    /**
     * 创建商户账户
     *
     * @param merchantNo   商户编号
     * @param merchantType 商户类型
     * @throws BizException .
     */
    public void createAccount(String merchantNo, int merchantType) throws BizException {
        logger.info("创建商户账户 merchantNo={} merchantType={}", merchantNo, merchantType);

        if (accountMchDao.getByMerchantNo(merchantNo) != null) {
            logger.error("merchantNo对应的商户账户记录已存在,merchantNo={}", merchantNo);
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("merchantNo对应的商户账户记录已存在");
        }

        AccountMch account = new AccountMch();
        account.setCreateTime(new Date());
        account.setVersion(0L);
        account.setModifyTime(new Date());
        account.setMerchantNo(merchantNo);
        account.setMerchantType(merchantType);
        account.setStatus(AccountStatusEnum.ACTIVE.getValue());
        account.setUnsettleAmount(BigDecimal.ZERO);
        account.setUsableAmount(BigDecimal.ZERO);
        accountMchDao.insert(account);
    }

    /**
     * 账户状态变更操作.
     *
     * @param merchantNo    用户编号.
     * @param accountStatus 账户状态
     * @param desc          变更操作说明.
     */
    public void changeAccountStatus(String merchantNo, int accountStatus, String desc) throws BizException {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        } else if (Arrays.stream(AccountStatusEnum.values()).noneMatch(p -> p.getValue() == accountStatus)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("无效的accountStatus");
        }

        AccountMch account = accountMchDao.getByMerchantNo(merchantNo);
        if (account == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("merchantNo对应的商户账户记录不已存在");
        }

        //当余额为0时，才能注销
        if (accountStatus == AccountStatusEnum.CANCELLED.getValue()) {
            if (AmountUtil.greater(account.getUnsettleAmount(), BigDecimal.ZERO) || AmountUtil.greater(account.getUsableAmount(), BigDecimal.ZERO)) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("该商户账户还有余额，不可注销");
            }
        }

        Integer oldStatus = account.getStatus();
        if (oldStatus == AccountStatusEnum.FREEZE_CREDIT.getValue() && accountStatus == AccountStatusEnum.FREEZE_DEBIT.getValue()) {
            //如果旧状态为冻结止收，新状态为冻结止付，则直接把账户状态变为 冻结中
            account.setStatus(AccountStatusEnum.FREEZING.getValue());
        } else if (oldStatus == AccountStatusEnum.FREEZE_DEBIT.getValue() && accountStatus == AccountStatusEnum.FREEZE_CREDIT.getValue()) {
            //如果旧状态为冻结止付，新状态为冻结止收，则直接把账户状态变为 冻结中
            account.setStatus(AccountStatusEnum.FREEZING.getValue());
        } else {
            account.setStatus(accountStatus);
        }
        logger.info("变更商户账户状态 merchantNo={} oldStatus={} newStatus={} desc={}", merchantNo, oldStatus, account.getStatus(), desc);
        accountMchDao.update(account);
    }

}
