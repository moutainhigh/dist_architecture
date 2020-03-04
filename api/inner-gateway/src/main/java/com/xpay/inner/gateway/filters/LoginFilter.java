package com.xpay.inner.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginFilter implements GlobalFilter, Ordered {

    public int getOrder(){
        return -30;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.如果访问的路径是白名单，则直接放行


        //2.访问路径非白名单，需要校验是否登陆

            //2.1 token不存在，直接跳转到登陆路径

            //2.2 token存在，则校验正确性，如果不正确，则直接返回错误信息


        //3.如果已登陆，则校验当前用户是否有访问权限

            //3.1 如果无访问权限，则直接返回错误信息


        return null;
    }

}
