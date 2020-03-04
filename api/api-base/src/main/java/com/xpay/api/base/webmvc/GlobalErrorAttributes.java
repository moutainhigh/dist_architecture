package com.xpay.api.base.webmvc;

import com.xpay.api.base.constants.HttpHeaderKey;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import com.xpay.api.base.utils.ResponseUtil;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.StringUtil;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用webmvc时的全局异常处理器，负责决定返回什么响应体
 */
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
        Throwable ex = getError(request);
        RequestParam requestParam = getRequestParam(request);
        ResponseParam response = getResponseParam(requestParam, ex);

        Map<String, Object> map = new HashMap<>();
        Field[] fields = ResponseParam.class.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field filed = fields[i];
            String name = filed.getName();

            filed.setAccessible(true);
            try{
                map.put(name, filed.get(response));
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    private RequestParam getRequestParam(WebRequest request){
        String mchNo = request.getHeader(HttpHeaderKey.REQUEST_MCH_NO_KEY);
        String signType = request.getHeader(HttpHeaderKey.REQUEST_SIGN_TYPE_KEY);

        RequestParam requestParam = new RequestParam();
        requestParam.setMch_no(mchNo==null ? "" : mchNo);
        requestParam.setSign_type(signType==null ? "" : signType);
        return requestParam;
    }

    private ResponseParam getResponseParam(RequestParam requestParam, Throwable ex){
        String mchNo = requestParam.getMch_no();
        String signType = requestParam.getSign_type();
        ResponseParam response;

        StringBuilder errMsg = new StringBuilder();
        if (ex instanceof BizException) {
            BizException e = (BizException) ex;
            response = new ResponseParam();
            response.setResp_code(e.getApiRespCode());
            response.setResp_msg(StringUtil.isEmpty(e.getApiErrCode()) ? e.getErrMsg() : e.getErrMsg()+"["+e.getApiErrCode()+"]");
            ResponseUtil.fillAcceptUnknownIfEmpty(response);//如果业务方没有设置，则设置受理未知
        } else if(isRequestFrameworkException(ex, errMsg)){
            response = new ResponseParam();
            response.setResp_code(ApiRespCodeEnum.PARAM_FAIL.getCode());
            response.setResp_msg(errMsg.toString());
        } else {
            response = ResponseParam.unknown(mchNo);
            response.setResp_msg("Internal Server Error");
        }

        response.setMch_no(mchNo);
        response.setSign_type(signType);
        return response;
    }

    private boolean isRequestFrameworkException(Throwable ex, StringBuilder strBuilder){
        if (ex instanceof NoHandlerFoundException) { //path路径不存在时
            strBuilder.append("请求路径错误，请检查method参数");
            return true;
        } else if (ex instanceof MethodArgumentNotValidException) { //hibernate-validator 参数校验不通过时
            FieldError fieldError = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError();
            String field = fieldError.getField();
            String msg = fieldError.getDefaultMessage();
            strBuilder.append("验参失败，"+field+":"+msg+"");
            return true;
        } else if (ex instanceof HttpMessageNotReadableException) {//参数类型转换错误时
            strBuilder.append("请求参数无法转换或读取，请检查参数类型");
            return true;
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {//用户传入的MediaType与系统在方法上设置的不一致时
            strBuilder.append("请参照接口文档选择合适的请求MediaType");
            return true;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {//用户请求方式与系统在方法上设置的不一致时，如：方法要求POST但用法使用GET请求
            strBuilder.append("请参照接口文档选择合适的HTTP Method");
            return true;
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            strBuilder.append("请参照接口文档选择合适的响应MediaType");
            return true;
        } else if (ex instanceof MissingPathVariableException) {
            strBuilder.append("请参照接口文档传入合适的uri参数");
            return true;
        } else if (ex instanceof MissingServletRequestParameterException) {
            strBuilder.append("请参照接口文档传入合适的请求参数");
            return true;
        } else if (ex instanceof ConversionNotSupportedException) {
            strBuilder.append("参数类型错误，请详细阅读相关接口文档");
            return true;
        } else if (ex instanceof TypeMismatchException) {
            strBuilder.append("参数类型错误，请详细阅读相关接口文档");
            return true;
        } else if (ex instanceof MissingServletRequestPartException) {
            strBuilder.append("系统不支持form-data或请求体内容有误");
            return true;
        }
        return false;
    }
}
