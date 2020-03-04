package com.xpay.api.base.utils;

import com.xpay.api.base.params.ResponseParam;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 响应处理工具类
 * @author chenyf
 * @date 2018-12-15
 */
public class ResponseUtil {

    public static void fillAcceptUnknownIfEmpty(ResponseParam response){
        if(StringUtil.isEmpty(response.getResp_code())){
            response.setResp_code(ApiRespCodeEnum.UNKNOWN.getCode());
        }
        if(StringUtil.isEmpty(response.getResp_msg())){
            response.setResp_msg(ApiRespCodeEnum.UNKNOWN.getMsg());
        }
    }

    public static Mono<Void> writeResponse(ServerHttpResponse response, ResponseParam responseParam){
        byte[] data = JsonUtil.toString(responseParam).getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = response.bufferFactory().wrap(data);
        return response.writeWith(Mono.just(dataBuffer));
    }

    public static void writeResponse(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        byte[] data = JsonUtil.toString(obj).getBytes(StandardCharsets.UTF_8);
        if(response instanceof ContentCachingResponseWrapper){
            response.resetBuffer();
            response.getOutputStream().write(data);
            ((ContentCachingResponseWrapper) response).copyBodyToResponse();
        }else{
            response.getOutputStream().write(data);
        }
    }
}
