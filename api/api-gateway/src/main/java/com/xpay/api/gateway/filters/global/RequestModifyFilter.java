package com.xpay.api.gateway.filters.global;

import com.alibaba.fastjson.JSON;
import com.xpay.api.gateway.filters.cache.CachedBodyOutputMessage;
import com.xpay.api.gateway.utils.IPUtil;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.api.gateway.helper.RequestHelper;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.api.base.constants.HttpHeaderKey;
import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @description 修改请求体，包括：sec_key解密 等
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class RequestModifyFilter extends AbstractGlobalFilter {
    @Autowired
    RequestHelper requestHelper;

    /**
     * 设置当前过滤器的执行顺序：本过滤器在全局过滤器中的顺序建议为第4个
     * @return
     */
    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_DECRYPT_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        RequestParam requestParam = (RequestParam) exchange.getAttributes().get(CACHE_REQUEST_BODY_OBJECT_KEY);

        //1.设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.set(HttpHeaderKey.REQUEST_MCH_IP_KEY, IPUtil.getIpAddr(exchange.getRequest()));//将请求IP放到请求头里
        headers.set(HttpHeaderKey.REQUEST_MCH_NO_KEY, requestParam.getMch_no());
        headers.set(HttpHeaderKey.REQUEST_SIGN_TYPE_KEY, requestParam.getSign_type());
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);//强制设置为application/json;charset=UTF-8
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        //2.对sec_key进行解密
        secKeyDecrypt(requestParam);

        //3.修改请求体
        modifyData(requestParam);

        //4.更新缓存中的body
        String bodyStr = JsonUtil.toString(requestParam);
        Mono<String> modifiedBody = Mono.just(bodyStr);
        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, requestParam);

        //5.更新转发到后端服务的body
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            long contentLength = headers.getContentLength();
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(headers);
                            if (contentLength > 0) {
                                httpHeaders.setContentLength(contentLength);
                            } else {
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            }
                            return httpHeaders;
                        }
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                }));
    }

    private boolean secKeyDecrypt(RequestParam requestParam){
        if(StringUtil.isEmpty(requestParam.getSec_key())){
            return false;
        }

        try{
            //如果sec_key不为空，则对sec_key进行解密，解密完成之后，再把RequestParam重新设置回缓存
            requestHelper.secKeyDecrypt(requestParam, new APIParam(requestParam.getSign_type(), requestParam.getVersion()));
            return true;
        }catch(Throwable ex){
            throw GatewayException.fail(ApiRespCodeEnum.PARAM_FAIL.getCode(), "sec_key解密失败", GatewayErrorCode.PARAM_CHECK_ERROR);
        }
    }

    private void modifyData(RequestParam requestParam){
        try{
        	//把string类型的data转换成JSONObject，是为了让后端服务能够实现参数自动注入
            requestParam.setData(JSON.parseObject((String) requestParam.getData()));
        }catch(Throwable ex){
            throw GatewayException.fail(ApiRespCodeEnum.PARAM_FAIL.getCode(), "data序列化失败，请确保为JSON格式，且没有特殊字符", GatewayErrorCode.PARAM_CHECK_ERROR);
        }
    }
}
