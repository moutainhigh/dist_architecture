package com.xpay.api.gateway.handler;

import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.api.gateway.helper.RequestHelper;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.config.conts.ReqCacheKey;
import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import com.xpay.api.base.utils.ResponseUtil;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 全局异常处理器
 * @author chenyf
 * @date 2019-02-23
 */
public class GatewayExceptionHandler extends DefaultErrorWebExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final String HTTP_STATUS_KEY = "httpStatus";

    @Autowired
    RequestHelper requestHelper;

    public GatewayExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                   ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = getErrorAttributes(request, includeStackTrace);
        HttpStatus errorStatus = getHttpStatus(error);
        if(error.containsKey(HTTP_STATUS_KEY)){
            error.remove(HTTP_STATUS_KEY);
        }

        return ServerResponse.status(errorStatus)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(error))
                .doOnNext((resp) -> logError(request, errorStatus));
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = super.getError(request);
        return buildResponse(request, error);
    }

    /**
     * 根据code获取对应的HttpStatus
     * @param errorAttributes
     */
    @Override
    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        HttpStatus httpStatus;
        if(errorAttributes.containsKey(HTTP_STATUS_KEY) && errorAttributes.get(HTTP_STATUS_KEY) != null){
            httpStatus = HttpStatus.valueOf((int) errorAttributes.get(HTTP_STATUS_KEY));
        }else{
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return httpStatus;
    }

    protected void logError(ServerRequest request, HttpStatus errorStatus) {
        Throwable ex = super.getError(request);
        RequestParam requestParam = (RequestParam) request.attributes().get(ReqCacheKey.CACHE_REQUEST_BODY_OBJECT_KEY);
        if(ex instanceof GatewayException){
            logger.error("网关处理过程中出现业务判断异常 Exception = {} RequestParam = {}", ((GatewayException) ex).toMsg(), JsonUtil.toString(requestParam));
        }else{
            logger.error("网关处理过程中出现未预期异常 RequestParam = {} ", JsonUtil.toString(requestParam), ex);
        }
    }

    /**
     * 构建返回的JSON数据格式，此处是处理gateway处理过程中本身的异常，后端服务的异常不会进入到这里
     *
     * @param request          请求体
     * @param ex               异常信息
     * @return
     */
    private Map<String, Object> buildResponse(ServerRequest request, Throwable ex) {
        HttpStatus httpStatus = getHttpStatus(ex);
        //当异常发生在读请求体的body之前时，此值为null，这种情况多半是因为路径参数不对，为避免有人恶意攻击，此处不再读取用户的商户信息来进行加签，直接返回空串
        RequestParam requestParam = (RequestParam) request.attributes().get(ReqCacheKey.CACHE_REQUEST_BODY_OBJECT_KEY);
        String version = requestParam != null ? requestParam.getVersion() : "";

        ResponseParam response = this.buildResponseParam(requestParam, ex);
        try{
            requestHelper.signAndEncrypt(response, new APIParam(response.getSign_type(), version));
        }catch(Throwable e){
            if(response.getSign() == null){
                response.setSign("");
            }
            logger.error("网关异常处理器中，添加签名时出现异常 RequestParam = {} ResponseParam = {}", JsonUtil.toString(requestParam), JsonUtil.toString(response), e);
        }

        //把响应内容转成Map
        Map<String, Object> respMap = new HashMap<>();
        respMap.put(HTTP_STATUS_KEY, httpStatus.value());
        Field[] fields = ResponseParam.class.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field filed = fields[i];
            String name = filed.getName();

            filed.setAccessible(true);
            try{
                respMap.put(name, filed.get(response));
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        return respMap;
    }

    private ResponseParam buildResponseParam(RequestParam requestParam, Throwable ex){
        String mchNo = requestParam == null ? "" : requestParam.getMch_no();
        String signType = requestParam == null ? "" : requestParam.getSign_type();
        ResponseParam response;

        if (ex instanceof GatewayException) {
            GatewayException e = (GatewayException) ex;
            response = new ResponseParam();
            response.setSign("");//默认为空串做签名内容
            response.setResp_code(e.getRespCode());
            response.setResp_msg(e.getRespMsg());
        } else if(ex instanceof TimeoutException) {
            response = ResponseParam.unknown(mchNo);
            response.setResp_msg("Time Out");
        } else if(ex instanceof NotFoundException) { //后端服务无法从注册中心被发现时
            response = new ResponseParam();
            response.setSign("");//默认为空串做签名内容
            response.setResp_code(ApiRespCodeEnum.PARAM_FAIL.getCode());
            response.setResp_msg("Service Not Found");
        } else if (ex instanceof ResponseStatusException) { //访问没有配置的route path时
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response = new ResponseParam();
            response.setSign("");//默认为空串做签名内容
            response.setResp_code(ApiRespCodeEnum.PARAM_FAIL.getCode());
            response.setResp_msg(responseStatusException.getMessage());
        } else {
            response = ResponseParam.unknown(mchNo);
            response.setResp_msg(response.getResp_msg() + ", Internal Server Error");
        }

        ResponseUtil.fillAcceptUnknownIfEmpty(response);

        response.setMch_no(mchNo);
        response.setSign_type(signType);
        return response;
    }

    private HttpStatus getHttpStatus(Throwable ex){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if(ex instanceof GatewayException){
            GatewayException e = (GatewayException) ex;
            if(GatewayErrorCode.PARAM_CHECK_ERROR == e.getErrCode()){
                httpStatus = HttpStatus.BAD_REQUEST;
            }else if(GatewayErrorCode.SIGN_VALID_ERROR == e.getErrCode()){
                httpStatus = HttpStatus.FORBIDDEN;
            }else if(GatewayErrorCode.IP_VALID_ERROR == e.getErrCode()){
                httpStatus = HttpStatus.FORBIDDEN;
            }else if(GatewayErrorCode.RATE_LIMIT_ERROR == e.getErrCode()){
                httpStatus = HttpStatus.TOO_MANY_REQUESTS;
            }else if(GatewayErrorCode.IP_BLACK_LIST == e.getErrCode()){
                httpStatus = HttpStatus.FORBIDDEN;
            }
        }else if (ex instanceof NotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            httpStatus = responseStatusException.getStatus();
        }
        return httpStatus;
    }
}
