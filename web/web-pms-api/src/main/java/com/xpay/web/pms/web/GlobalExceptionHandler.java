package com.xpay.web.pms.web;

import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器
 *
 * @author linguangsheng
 * @date 2019/10/31
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理参数校验异常
     * @param e     异常
     * @return      response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResult<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 错误信息
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getDefaultMessage()).append("|");
        }
        if (errorMsg.length() > 0) {
            errorMsg.deleteCharAt(errorMsg.length() - 1);
        }

        logger.warn("== 参数错误, {}", errorMsg.toString());
        return RestResult.error(errorMsg.toString());
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public RestResult<String> handleHttpMessageConversionException(HttpMessageConversionException e) {
        logger.warn("== 参数转换错误", e);
        return RestResult.error("参数错误");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public RestResult<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.warn("== 参数类型不匹配, name={}", e.getName());
        return RestResult.error("参数错误");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RestResult<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.warn("== 参数缺失, name={}, type={}", e.getParameterName(), e.getParameterType());
        return RestResult.error("参数缺失");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public RestResult<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("== {}方法不支持", e.getMethod());
        return RestResult.error("请求出错");
    }


    @ExceptionHandler(BizException.class)
    public RestResult<String> handleBizException(BizException e) {
        logger.error("业务异常", e);
        return RestResult.error(e.getErrMsg());
    }

    @ExceptionHandler(Exception.class)
    public RestResult<String> handleException(Exception e) {
        logger.error("系统异常", e);
        return RestResult.error("系统异常");
    }
}
