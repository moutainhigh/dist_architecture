package com.xpay.common.statics.exception;

/**
 * Author: Cmf
 * Date: 2019/10/22
 * Time: 18:07
 * Description:
 */
public class SequenceExceptions {


    //SEQUENCE通用异常
    public static BizException SEQUENCE_COMMON_EXCEPTION = new BizException(504000001, false);

    //SEGMENT服务初始化异常
    public static BizException SEGMENT_SERVICE_INIT_FAIL = new BizException(504000002, false);


}
