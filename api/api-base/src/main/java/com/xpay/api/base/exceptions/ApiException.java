package com.xpay.api.base.exceptions;

import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.statics.exceptions.BizException;

public class ApiException extends BizException {

    /**
     * 参数校验失败时抛出的异常
     * @param errCode
     * @param errMsg
     * @return
     */
    public static ApiException paramValidFail(String errCode, String errMsg){
        ApiException ex = new ApiException();
        ex.setApiRespCode(ApiRespCodeEnum.PARAM_FAIL.getCode());
        ex.setErrMsg(errMsg + "[" + errCode + "]");
        return ex;
    }

    /**
     * 业务规则校验失败时抛出的异常
     * @param errCode
     * @param errMsg
     * @return
     */
    public static ApiException bizValidFail(String errCode, String errMsg){
        ApiException ex = new ApiException();
        ex.setApiRespCode(ApiRespCodeEnum.BIZ_FAIL.getCode());
        ex.setErrMsg(errMsg + "[" + errCode + "]");
        return ex;
    }
}
