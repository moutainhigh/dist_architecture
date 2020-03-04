package com.xpay.common.statics.exception;

/**
 * Author: Cmf
 * Date: 2020.2.12
 * Time: 18:14
 * Description:
 */
public class CommonExceptions {
    public static final BizException PARAM_INVALID = new BizException(100001001, false);

    public static final BizException BIZ_INVALID = new BizException(100001002, false);

    public static final BizException DB_AFFECT_ROW_NOT_MATCH = new BizException(100001003, false);

    //todo 账务检查
    public static final BizException UNEXPECT_ERROR = new BizException(100001004, false);

    public static final BizException CONNECT_ERROR = new BizException(100001005, true);
}
