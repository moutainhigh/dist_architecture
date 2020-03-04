package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.common.util.utils.StringUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.gateway.config.conts.ReqCacheKey;
import com.xpay.api.base.params.RequestParam;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @description 重写请求的path路径
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class RewritePathFilter extends AbstractGlobalFilter {
    /**
     * 设置当前过滤器的执行顺序：本过滤器在全局过滤器中的顺序建议为第5个，目前来说是在转发到后端服务之前的最后一个动作
     * @return
     */
    @Override
    public int getOrder() {
        return FilterOrder.REWRITE_PATH_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        RequestParam requestParam = (RequestParam) exchange.getAttributes().get(CACHE_REQUEST_BODY_OBJECT_KEY);
        ServerHttpRequest req = exchange.getRequest();
        String originalPath = req.getURI().getPath();

        String newPath = getPathFromMethod(requestParam.getMethod());
        ServerHttpRequest request = req.mutate()
                .path(newPath)
                .build();

        newPath = subPathEnd(newPath, "/", 0);
        URI uri = request.getURI();

        exchange.getAttributes().put(ReqCacheKey.GATEWAY_ORIGINAL_REQUEST_PATH_ATTR, originalPath);
        exchange.getAttributes().put(ReqCacheKey.GATEWAY_ORIGINAL_REQUEST_FULL_PATH_ATTR, originalPath + newPath);
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, uri);
        return chain.filter(exchange.mutate().request(request).build());
    }

    private String getPathFromMethod(String method){
        String path = "/";
        if(StringUtil.isNotEmpty(method)){
            path = method.replace(".", "/");
            if(! path.startsWith("/")){
                path = "/" + path;
            }
        }
        return path;
    }
}
