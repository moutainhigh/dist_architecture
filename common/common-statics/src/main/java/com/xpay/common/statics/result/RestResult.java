package com.xpay.common.statics.result;

/**
 * Author: Cmf
 * Date: 2019/10/23
 * Time: 19:15
 * Description:
 */
public class RestResult<T> {
    private static final int SUCCESS = 20000;
    private static final int COMMON_ERROR = 20001;
    private static final int PERMISSION_DENY = 20002;
    private static final int INVALID_TOKEN = 20003;

    private T data;
    private int code;
    /**
     * message只用于存在错误信息，处理成功的提示信息请放在data中
     */
    private String message;

    private RestResult(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> RestResult<T> deny(String message) {
        return new RestResult<>(null, PERMISSION_DENY, message);
    }

    public static <T> RestResult<T> error(String message) {
        return new RestResult<>(null, COMMON_ERROR, message);
    }

    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(data, SUCCESS, "");
    }

    public static RestResult unAuth(String message) {
        return new RestResult<>(null, INVALID_TOKEN, message);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
