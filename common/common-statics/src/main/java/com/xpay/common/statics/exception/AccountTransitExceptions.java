package com.xpay.common.statics.exception;

/**
 * Author: Cmf
 * Date: 2020.3.2
 * Time: 9:26
 * Description: 在途资金账务相关的异常
 */
public class AccountTransitExceptions {

    /**
     * 账户金额业务计算错误
     */
    public static final BizException ACCOUNT_AMOUNT_BIZ_CALC_ERROR = new BizException(502000001, false);

    /**
     * 在途余额不足
     */
    public static final BizException TRANSIT_AMOUNT_NOT_ENOUGH = new BizException(502000002, false);

    /**
     * 待账务处理表的账务处理流水号重复
     */
    public static final BizException ACCOUNT_PROCESS_PENDING_PROCESS_NO_REPEAT = new BizException(502000003, false);

    /**
     * 待账务处理记录已存在
     */
    public static final BizException ACCOUNT_PROCESS_PENDING_UNIQUE_KEY_REPEAT = new BizException(502000004, false);
    /**
     * 账户记录不存在
     */
    public static final BizException ACCOUNT_RECORD_NOT_EXIT = new BizException(502000005, false);
    /**
     * 账户状态处于"禁用"
     */
    public static final BizException ACCOUNT_STATUS_IS_INACTIVE = new BizException(502000006, false);

    /**
     * 重复账务处理
     */
    public static final BizException ACCOUNT_PROCESS_REPEAT = new BizException(502000007, false);
    /**
     * 锁账户失败
     */
    public static final BizException ACQUIRE_LOCK_FAIL = new BizException(502000008, false);

}
