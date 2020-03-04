package com.xpay.service.accountsub.biz;

import com.xpay.common.statics.enums.account.AccountStatusEnum;
import com.xpay.common.statics.enums.merchant.MerchantTypeEnum;
import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.AmountUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.accountsub.entity.AccountSub;
import com.xpay.service.accountsub.dao.AccountSubDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 账户管理biz
 *
 * @author chenyf
 */
@Component
public class AccountSubManageBiz {
    private Logger logger = LoggerFactory.getLogger(AccountSubManageBiz.class);

    @Autowired
    private AccountSubDao accountSubDao;

    /**
     * 创建子商户账户
     *
     * @param merchantNo       子商户编号
     * @param merchantType     .
     * @param parentMerchantNo 父商户编号
     * @throws com.xpay.common.statics.exception.BizException .
     */
    public void createAccount(String merchantNo, Integer merchantType, String parentMerchantNo) throws BizException {
        logger.info("创建子商户账户 merchantNo={} merchantType={} parentMerchantNo={}", merchantNo, merchantNo, parentMerchantNo);

        if (accountSubDao.getByMerchantNo(merchantNo) != null) {
            logger.error("merchantNo对应的子商户账户记录已存在,merchantNo={}", merchantNo);
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("merchantNo对应的子商户账户记录已存在");
        } else if (merchantType == null || merchantType != MerchantTypeEnum.SUB_MERCHANT.getValue()) {
            logger.error("不支持的merchantType,merchantType={}", merchantType);
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("不支持的merchantType");
        }

        AccountSub account = new AccountSub();
        account.setCreateTime(new Date());
        account.setVersion(0L);
        account.setModifyTime(new Date());
        account.setMerchantNo(merchantNo);
        account.setMerchantType(merchantType);
        account.setParentMerchantNo(parentMerchantNo);
        account.setStatus(AccountStatusEnum.ACTIVE.getValue());
        account.setUnsettleAmount(BigDecimal.ZERO);
        account.setUsableAmount(BigDecimal.ZERO);
        accountSubDao.insert(account);
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

        AccountSub account = accountSubDao.getByMerchantNo(merchantNo);
        if (account == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("merchantNo对应的子商户账户记录不已存在");
        }

        //当余额为0时，才能注销
        if (accountStatus == AccountStatusEnum.CANCELLED.getValue()) {
            if (AmountUtil.greater(account.getUnsettleAmount(), BigDecimal.ZERO) || AmountUtil.greater(account.getUsableAmount(), BigDecimal.ZERO)) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("该子商户账户还有余额，不可注销");
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
        logger.info("变更子商户账户状态 merchantNo={} oldStatus={} newStatus={} desc={}", merchantNo, oldStatus, account.getStatus(), desc);
        accountSubDao.update(account);
    }

}
