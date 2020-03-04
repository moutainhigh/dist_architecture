package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.utils.IPUtil;
import com.xpay.api.gateway.config.PropertiesConfig;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * @description IP黑名单过滤器
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class IPBlackListFilter extends AbstractGlobalFilter {
    private Logger logger = LoggerFactory.getLogger(IPBlackListFilter.class);
    @Autowired
    PropertiesConfig propertiesConfig;

    @Override
    public int getOrder() {
        return FilterOrder.IP_BLACKLIST_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = IPUtil.getIpAddr(exchange.getRequest());
        if(StringUtil.isEmpty(propertiesConfig.getIpBlackListPattern()) || isPass(propertiesConfig.getIpBlackListPattern(), ip)){
            return chain.filter(exchange);
        }

        logger.warn("ip = {} 被列为黑名单，禁止访问！ pattern = {}", ip, propertiesConfig.getIpBlackListPattern());
        throw GatewayException.fail(ApiRespCodeEnum.SYS_FORBID.getCode(), "请求受限", GatewayErrorCode.IP_BLACK_LIST);
    }

    private boolean isPass(String pattern, String ip){
        if(Pattern.matches(pattern, ip)){
            return false;
        }else{
            return true;
        }
    }
}
