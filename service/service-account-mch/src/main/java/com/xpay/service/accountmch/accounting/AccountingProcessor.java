package com.xpay.service.accountmch.accounting;

import com.xpay.common.statics.enums.account.AccountProcessTypeEnum;
import com.xpay.common.statics.enums.account.AccountStatusEnum;
import com.xpay.common.statics.exception.AccountMchExceptions;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accountmch.dto.AccountMchProcessDto;
import com.xpay.facade.accountmch.dto.AccountMchRequestDto;
import com.xpay.facade.accountmch.entity.AccountMch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author luobinzhao
 * @date 2020/1/14 15:02
 */
public abstract class AccountingProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 执行账务处理的方法
     *
     * @param account    账务处理流水号
     * @param requestDto 账户记录
     * @param processDto 账务处理业务对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void process(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto) {
        //1.校验账户状态
        this.validateAccountStatus(account, processDto.getProcessType());

        //2.初始化一些参数值
        processDto.setAccountTime(DateUtil.getDateWithoutMills(new Date()));

        //3.执行账务处理
        this.doProcess(account, requestDto, processDto);

        //4.校验账务处理结果的正确性
        AccountingHelper.validateAccountAmountCalcResult(requestDto.getAccountProcessNo(), account);
    }

    /**
     * 执行账务处理的方法
     *
     * @param account    账务处理流水号
     * @param requestDto 账户记录
     * @param processDto 账务处理业务对象
     */
    protected abstract void doProcess(AccountMch account, AccountMchRequestDto requestDto, AccountMchProcessDto processDto);

    /**
     * 复制账户对象
     *
     * @param account .
     * @return .
     */
    public static AccountMch copyAccount(AccountMch account) {
        AccountMch copy = new AccountMch();
        BeanUtil.copyProperties(account, copy);
        return copy;
    }

    /**
     * 校验账户状态
     *
     * @param account     .
     * @param processType .
     */
    protected void validateAccountStatus(AccountMch account, Integer processType) {
        if (account.getStatus() == AccountStatusEnum.CANCELLED.getValue()) {
            throw AccountMchExceptions.ACCOUNT_STATUS_IS_INACTIVE.newWithErrMsg("账户已注销");
        } else if (processType == AccountProcessTypeEnum.CREDIT.getValue()) {
            if (account.getStatus() == AccountStatusEnum.FREEZING.getValue() || account.getStatus() == AccountStatusEnum.FREEZE_CREDIT.getValue()) {
                throw AccountMchExceptions.ACCOUNT_STATUS_IS_INACTIVE.newWithErrMsg("账户状态异常,用户编号{" + account.getMerchantNo() + "},账户状态{" + account.getStatus() + "}");
            }
        } else if (processType == AccountProcessTypeEnum.DEBIT.getValue()) {
            if (account.getStatus() == AccountStatusEnum.FREEZING.getValue() || account.getStatus() == AccountStatusEnum.FREEZE_DEBIT.getValue()) {
                throw AccountMchExceptions.ACCOUNT_STATUS_IS_INACTIVE.newWithErrMsg("账户状态异常,用户编号{" + account.getMerchantNo() + "},账户状态{" + account.getStatus() + "}");
            }
        }
    }

}
