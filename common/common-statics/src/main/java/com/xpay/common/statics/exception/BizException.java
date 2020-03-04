package com.xpay.common.statics.exception;

/**
 * Author: Cmf
 * Date: 2020.2.12
 * Time: 17:50
 * Description:
 */
public class BizException extends RuntimeException {
    private int sysErrorCode;
    private String apiErrorCode;
    private String errMsg;
    private boolean mqRetry;

    public int getSysErrorCode() {
        return sysErrorCode;
    }

    public String getApiErrorCode() {
        return apiErrorCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public boolean isMqRetry() {
        return mqRetry;
    }

    private BizException() {

    }


    BizException(int sysErrorCode, boolean mqRetry) {
        this.sysErrorCode = sysErrorCode;
        this.mqRetry = mqRetry;
    }

    BizException(int sysErrorCode, String apiErrorCode, boolean mqRetry) {
        this.sysErrorCode = sysErrorCode;
        this.apiErrorCode = apiErrorCode;
        this.mqRetry = mqRetry;
    }

    public BizException newWithErrMsg(String errMsg) {
        BizException bizException = new BizException();
        bizException.sysErrorCode = this.sysErrorCode;
        bizException.apiErrorCode = this.apiErrorCode;
        bizException.errMsg = errMsg;
        bizException.mqRetry = this.mqRetry;
        return bizException;
    }


}
