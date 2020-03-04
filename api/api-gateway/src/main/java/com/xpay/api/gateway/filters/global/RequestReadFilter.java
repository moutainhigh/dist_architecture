package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.base.params.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.TreeMap;

/**
 * @description 读请求体，并转换成RequestParam，之后再放到缓存中存起来，这个过滤器必须是第1个执行，不然，后续的过滤器无法获得请求参数
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class RequestReadFilter extends AbstractGlobalFilter{
    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();
    /**
     * 设置当前过滤器的执行顺序：本过滤器在全局过滤器中的顺序必须为第1个，不然，后续的过滤器无法获得请求参数
     * @return
     */
    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_READ_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getHeaders().getContentType() == null) {
            return chain.filter(exchange);
        } else {
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        //Update the retain counts so we can read the body twice, once to parse into an object
                        //that we can test the predicate against and a second time when the HTTP client sends
                        //the request downstream
                        //Note: if we end up reading the body twice we will run into a problem, but as of right
                        //now there is no good use case for doing this
                        DataBufferUtils.retain(dataBuffer);
                        //Make a slice for each read so each read has its own read/write indexes
                        Flux<DataBuffer> cachedFlux = Flux
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));

                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };

                        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                        return ServerRequest.create(mutatedExchange, messageReaders)
                                .bodyToMono(String.class)
                                .doOnNext(objectValue -> { cacheRequestParam(objectValue, exchange); })
                                .then(chain.filter(mutatedExchange));
                    });
        }
    }

    private void cacheRequestParam(String bodyStr, ServerWebExchange exchange){
        if (StringUtil.isEmpty(bodyStr)) {
            return;
        }

        RequestParam requestParam;
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED.includes(contentType)) {
            TreeMap<String, Object> paramMap = decodeFromUrlParam(bodyStr);
            bodyStr = JsonUtil.toString(paramMap);
            requestParam = JsonUtil.toBean(bodyStr, RequestParam.class);
        }else if(MediaType.APPLICATION_JSON_UTF8.includes(contentType)){
            requestParam = JsonUtil.toBean(bodyStr, RequestParam.class);
        }else{
            throw GatewayException.fail(ApiRespCodeEnum.PARAM_FAIL.getCode(), "仅支持application/x-www-form-urlencoded和application/json;charset=UTF-8两种报文格式", GatewayErrorCode.PARAM_CHECK_ERROR);
        }
        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, requestParam);
    }

    private TreeMap<String, Object> decodeFromUrlParam(String urlParam){
        TreeMap<String, Object> paramMap = new TreeMap<>();//用TreeMap维持参数的顺序
        String[] formData = urlParam.split("&");
        for (String data : formData) {
            String[] formDataTemp = data.split("=");
            try{
                String value = URLDecoder.decode(formDataTemp.length > 1 ? formDataTemp[1] : "", "utf-8");
                paramMap.put(formDataTemp[0], value);
            }catch(UnsupportedEncodingException e){
                throw GatewayException.fail(ApiRespCodeEnum.PARAM_FAIL.getCode(), "未支持的编码格式", GatewayErrorCode.PARAM_CHECK_ERROR);
            }
        }
        return paramMap;
    }
}
