package com.xpay.common.statics.exceptions;

import com.xpay.common.statics.enums.common.ApiRespCodeEnum;

import java.io.Serializable;

/**
 * 业务异常类
 * Created by chenyf on 2019/10/31.
 */
public class BizException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -345568986985960990L;

    /**
     * 对外API的响应码 {@link ApiRespCodeEnum}
     */
    protected String apiRespCode;
    /**
     * 对外API的业务异常码(由每个业务自行定义)
     */
    protected String apiErrCode;
    /**
     * 系统内部的错误码
     */
    protected int sysErrCode;
    /**
     * 错误描述
     */
    protected String errMsg;


    /**
     * 参数校验不通过
     */
    public final static int PARAM_INVALIDATE = 100001001;
    /**
     * 业务流程校验异常
     */
    public final static int BIZ_INVALIDATE = 100001002;
    /**
     * 数据库实际处理（插入行数，更新行数，删除行数）的行数与预期不匹配
     */
    public final static int DB_AFFECT_ROW_NOT_MATCH = 100001003;

    /**
     * 未预期异常
     */
    public final static int UNEXPECT_ERROR = 100001004;
    /**
     * 可丢弃MQ消息的码
     */
    public final static int MQ_DISCARDABLE = 100001005;


    public BizException() {
        super();
    }

    public BizException(String apiRespCode, String apiErrCode, String message) {
        this(apiRespCode, apiErrCode, 0, message);
    }

    public BizException(String apiRespCode, String message) {
        this(apiRespCode, null, 0, message);
    }

    public BizException(int sysErrCode, String message) {
        this(null, null, sysErrCode, message);
    }

    public BizException(String apiRespCode, String apiErrCode, int sysErrCode, String message) {
        super(message);
        this.apiRespCode = apiRespCode;
        this.apiErrCode = apiErrCode;
        this.sysErrCode = sysErrCode;
        this.errMsg = message;
    }

    public BizException(Throwable cause) {
        this(null, cause);
    }

    public BizException(String message) {
        super(message);
        this.errMsg = message;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.errMsg = message;
    }

    public String getApiRespCode() {
        return apiRespCode;
    }

    public void setApiRespCode(String apiRespCode) {
        this.apiRespCode = apiRespCode;
    }

    public String getApiErrCode() {
        return apiErrCode;
    }

    public void setApiErrCode(String apiErrCode) {
        this.apiErrCode = apiErrCode;
    }

    public int getSysErrCode() {
        return sysErrCode;
    }

    public void setSysErrCode(int sysErrCode) {
        this.sysErrCode = sysErrCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static BizException bizFailResp(String apiErrCode, String errMsg){
        return new BizException(ApiRespCodeEnum.BIZ_FAIL.getCode(), apiErrCode, 0, errMsg);
    }
    public static BizException paramFailResp(String apiErrCode, String errMsg){
        return new BizException(ApiRespCodeEnum.PARAM_FAIL.getCode(), apiErrCode, 0, errMsg);
    }
}
