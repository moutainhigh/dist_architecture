package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.config.PropertiesConfig;
import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @description 业务停用过滤器，主要用以某些业务线需要内部维护而要求停止对外服务时使用
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class BizOffFilter extends AbstractGlobalFilter {
    private Logger logger = LoggerFactory.getLogger(BizOffFilter.class);
    @Autowired
    PropertiesConfig propertiesConfig;

    @Override
    public int getOrder() {
        return FilterOrder.BIZ_OFF_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String originalPath = subPathEnd(req.getURI().getPath(), "/", 0);
        String offBuss = propertiesConfig.getOffBuss();

        if(this.isReject(originalPath, offBuss)){
            logger.warn("originalPath = {} offBuss = {} 业务受限，业务配置禁止访问", originalPath, offBuss);
            throw GatewayException.fail(ApiRespCodeEnum.SYS_FORBID.getCode(), "系统维护", GatewayErrorCode.REQUEST_FORBID);
        }else{
            return chain.filter(exchange);
        }
    }

    private boolean isReject(String path, String offBuss){
        if(StringUtil.isEmpty(offBuss)){
            return false;
        }if("ALL".equals(offBuss) || offBuss.contains(path)){
            return true;
        }else{
            return false;
        }
    }
}
