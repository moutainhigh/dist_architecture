package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import com.xpay.api.gateway.config.conts.ReqCacheKey;

/**
 * @description 全局过滤器抽象类，负责处理子类全局过滤器的一些公共逻辑
 * @author chenyf
 * @date 2019-02-23
 */
public abstract class AbstractGlobalFilter implements GlobalFilter, Ordered {
    protected static final String CACHE_REQUEST_BODY_OBJECT_KEY = ReqCacheKey.CACHE_REQUEST_BODY_OBJECT_KEY;

    protected String subPathEnd(String path, String pattern, int count){
        if(count > 3){//避免商户不规范传入url时进入死循环
            throw GatewayException.fail(ApiRespCodeEnum.PARAM_FAIL.getCode(), "请求路径不正确", GatewayErrorCode.PARAM_CHECK_ERROR);
        }else if(path.endsWith(pattern)){
            path = path.substring(0, path.length()-1);
            return subPathEnd(path, pattern, count+1);
        }else{
            return path;
        }
    }
}
