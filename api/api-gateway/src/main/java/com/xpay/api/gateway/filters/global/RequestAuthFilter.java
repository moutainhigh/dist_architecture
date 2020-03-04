package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.utils.IPUtil;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.api.gateway.helper.RequestHelper;
import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.service.ValidService;
import reactor.core.publisher.Mono;

/**
 * @description 请求体鉴权校验，包括：签名校验 等等
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class RequestAuthFilter extends AbstractGlobalFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RequestHelper requestHelper;
    @Autowired
    ValidService validFailService;

    /**
     * 设置当前过滤器的执行顺序：本过滤器在全局过滤器中的顺序建议为第3个，因为，如果鉴权不通过，就没有必要进行后续的过滤器处理了
     * @return
     */
    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_AUTH_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        boolean isVerifyOk = false;//默认为false，勿改此默认值
        String ip = IPUtil.getIpAddr(request);
        RequestParam requestParam = (RequestParam) exchange.getAttributes().get(CACHE_REQUEST_BODY_OBJECT_KEY);
        APIParam apiParam = new APIParam(requestParam.getSign_type(), requestParam.getVersion());
        Throwable cause = null;

        //1.签名校验
        try{
            isVerifyOk = requestHelper.signVerify(requestParam, apiParam);
        }catch (Throwable e){
            logger.error("签名校验失败 RequestParam = {}", JsonUtil.toString(requestParam), e);
        }

        //2.商户状态校验
        if(isVerifyOk){
            try{
                isVerifyOk = requestHelper.mchVerify(requestParam, apiParam);
            }catch (Throwable e){
                logger.error("商户状态不可用 RequestParam = {}", JsonUtil.toString(requestParam), e);
            }
        }

        //3.如果校验失败，则进行日志打印、邮件通知等处理
        if(! isVerifyOk){
            try{
                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                validFailService.afterSignValidFail(route.getId(), ip, requestParam, cause);
            }catch(Throwable e){
                logger.error("验签失败，验签失败后处理器有异常 RequestParam = {}", JsonUtil.toString(requestParam), e);
            }
        }

        if(isVerifyOk){
            return chain.filter(exchange);
        }else{
            //抛出异常，由全局异常处理器来处理响应信息
            throw GatewayException.fail(ApiRespCodeEnum.SIGN_FAIL.getCode(), "验签失败", GatewayErrorCode.SIGN_VALID_ERROR);
        }
    }
}
