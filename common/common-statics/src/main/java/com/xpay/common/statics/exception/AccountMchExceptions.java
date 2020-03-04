package com.xpay.common.statics.exception;

/**
 * Author: Cmf
 * Date: 2020.2.12
 * Time: 18:14
 * Description:平台商户账务异常
 */
public class AccountMchExceptions {


    public static final BizException ACCOUNT_AMOUNT_BIZ_CALC_ERROR = new BizException(500003001, false);

    /**
     * 可用余额不足
     */
    public static final BizException USABLE_AMOUNT_NOT_ENOUGH = new BizException(500003002, false);

    /**
     * 待账务处理表的账务处理流水号重复
     */
    public static final BizException ACCOUNT_PROCESS_PENDING_PROCESS_NO_REPEAT = new BizException(500003003, false);

    /**
     * 待账务处理记录已存在
     */
    public static final BizException ACCOUNT_PROCESS_PENDING_UNIQUE_KEY_REPEAT = new BizException(500003004, false);

    /**
     * 账户记录不存在
     */
    public static final BizException ACCOUNT_RECORD_NOT_EXIT = new BizException(500003005, false);

    /**
     * 待清算金额不足
     */
    public static final BizException UNSETTLE_AMOUNT_NOT_ENOUGH = new BizException(500003006, false);

    /**
     * 账户状态处于"禁用"
     */
    public static final BizException ACCOUNT_STATUS_IS_INACTIVE = new BizException(500003007, false);

    /**
     * 重复账务处理
     */
    public static final BizException ACCOUNT_PROCESS_REPEAT = new BizException(500003008, false);

    /**
     * 锁账户失败
     */
    public static final BizException ACQUIRE_LOCK_FAIL = new BizException(500003009, false);

}
