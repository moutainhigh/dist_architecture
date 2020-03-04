package com.xpay.api.base.webflux;

import com.xpay.api.base.constants.HttpHeaderKey;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import com.xpay.api.base.utils.ResponseUtil;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.StringUtil;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用webflux时的全局异常处理器，负责决定返回什么响应体
 */
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
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

    private RequestParam getRequestParam(ServerRequest request){
        List<String> mchs = request.headers().header(HttpHeaderKey.REQUEST_MCH_NO_KEY);
        List<String> signTypes = request.headers().header(HttpHeaderKey.REQUEST_SIGN_TYPE_KEY);

        RequestParam requestParam = new RequestParam();
        requestParam.setMch_no((mchs==null || mchs.isEmpty()) ? "" : mchs.get(0));
        requestParam.setSign_type((signTypes==null || signTypes.isEmpty()) ? "" : signTypes.get(0));
        return requestParam;
    }

    private ResponseParam getResponseParam(RequestParam requestParam, Throwable ex){
        String mchNo = requestParam.getMch_no();
        String signType = requestParam.getSign_type();
        ResponseParam response;

        if (ex instanceof BizException) {
            BizException e = (BizException) ex;
            response = new ResponseParam();
            response.setResp_code(e.getApiRespCode());
            response.setResp_msg(StringUtil.isEmpty(e.getApiErrCode()) ? e.getErrMsg() : e.getErrMsg()+"["+e.getApiErrCode()+"]");
            ResponseUtil.fillAcceptUnknownIfEmpty(response);
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response = new ResponseParam();
            response.setResp_code(ApiRespCodeEnum.PARAM_FAIL.getCode());
            response.setResp_msg(responseStatusException.getReason());
        } else {
            response = ResponseParam.unknown(mchNo);
            response.setResp_msg("Internal Server Error");
        }

        response.setMch_no(mchNo);
        response.setSign_type(signType);
        return response;
    }
}
