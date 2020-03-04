package com.xpay.api.gateway.fallback;

import com.xpay.api.gateway.config.conts.ReqCacheKey;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import reactor.core.publisher.Mono;

/**
 * hystrix的熔断、降级时会进入到此处
 * @author chenyf
 */
@RestController
public class FallbackController {

    /**
     * 有配置Hystrix过滤器时，发生熔断或降级时会进入此方法
     * @param exchange
     * @return
     */
    @RequestMapping("/fallback")
    public Mono<ResponseParam> fallback(ServerWebExchange exchange) {
        RequestParam requestParam = (RequestParam) exchange.getAttributes().get(ReqCacheKey.CACHE_REQUEST_BODY_OBJECT_KEY);
        String mchNo = requestParam == null ? "" : requestParam.getMch_no();
        String signType = requestParam == null ? "" : requestParam.getSign_type();

        //因为调用后端服务超时的时候也会进入fallback，此时，我们并不知道业务处理结果，所以，统一返回"受理未知"响应信息
        ResponseParam responseParam = ResponseParam.unknown(mchNo);
        responseParam.setSign_type(signType);
        responseParam.setResp_msg("服务繁忙，结果未知");
        return Mono.just(responseParam);
    }
}
