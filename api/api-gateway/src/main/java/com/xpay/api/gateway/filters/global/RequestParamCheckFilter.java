package com.xpay.api.gateway.filters.global;

import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.statics.enums.common.SignTypeEnum;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.common.util.utils.ValidateUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.base.params.RequestParam;
import reactor.core.publisher.Mono;

/**
 * @description 请求参数校验，这个过滤器必须是在第2个，不然，后续的过滤器可能会会获取到错误的参数，或者因为某个参数为null而报空指针
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class RequestParamCheckFilter extends AbstractGlobalFilter {

    /**
     * 设置当前过滤器的执行顺序：本过滤器在全局过滤器中的顺序必须为第2个，不然，后续的过滤器拿取参数时可能会出现空指针异常
     * @return
     */
    @Override
    public int getOrder() {
        return FilterOrder.REQUEST_PARAM_CHECK_FILTER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Object object = exchange.getAttributes().get(CACHE_REQUEST_BODY_OBJECT_KEY);
        RequestParam requestParam = null;
        String msg;
        if(object != null && object instanceof RequestParam){
            requestParam = (RequestParam) object;
        }

        msg = requestPathValid(exchange.getRequest().getURI().getPath());
        if(StringUtil.isEmpty(msg)){
            msg = paramValid(requestParam);
        }

        if(StringUtil.isEmpty(msg)){
            return chain.filter(exchange);
        }else{
            //抛出异常，由全局异常处理器来处理响应信息
            throw GatewayException.fail(ApiRespCodeEnum.PARAM_FAIL.getCode(), msg, GatewayErrorCode.PARAM_CHECK_ERROR);
        }
    }

    public String requestPathValid(String requestPath){
        if(StringUtil.isEmpty(requestPath) || "/".equals(requestPath.trim())){
            return "请求路径不能为空";
        }else{
            return "";
        }
    }

    public String paramValid(RequestParam requestParam){
        if(requestParam == null){
            return "参数请求体为空！";
        }else if(StringUtil.isEmpty(requestParam.getMethod())){
            return "method 为空！";
        }else if(StringUtil.isEmpty(requestParam.getVersion())){
            return "version 为空！";
        } else if (requestParam.getData() == null || StringUtil.isEmpty(requestParam.getData().toString())) {
            return "data 为空！";
        } else if (StringUtil.isEmpty(requestParam.getRand_str())) {
            return "rand_str 为空！";
        } else if (StringUtil.isEmpty(requestParam.getSign_type())) {
            return "sign_type 为空！";
        } else if (StringUtil.isEmpty(requestParam.getMch_no())) {
            return "mch_no 为空！";
        } else if (StringUtil.isEmpty(requestParam.getSign())) {
            return "sign 为空！";
        }

        if(! (requestParam.getData() instanceof String)){
            return "data 只能是string类型！";
        }else if(StringUtil.isLengthOver(requestParam.getMethod(), 64)){
            return "method 的长度不能超过64！";
        }else if(StringUtil.isLengthOver(requestParam.getVersion(), 5)){
            return "version 的长度不能超过5！";
        }else if(! StringUtil.isLengthOk(requestParam.getRand_str(), 32,32)){
            return "rand_str 的长度须为32！";
        }else if(StringUtil.isLengthOver(requestParam.getSign_type(), 5)){
            return "sign_type 的长度不能超过5！";
        }else if(! StringUtil.isLengthOk(requestParam.getMch_no(), 10, 15)){
            return "mch_no 的长度须在10~15之间！";
        }

        if (!ValidateUtil.isInteger(requestParam.getSign_type()) || SignTypeEnum.getEnum(Integer.parseInt(requestParam.getSign_type())) == null) {
            return "sign_type 非法参数值 !!";
        }
        return "";
    }
}
