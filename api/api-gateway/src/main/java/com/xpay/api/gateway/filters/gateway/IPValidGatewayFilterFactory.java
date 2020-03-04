package com.xpay.api.gateway.filters.gateway;

import com.xpay.api.gateway.utils.IPUtil;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.api.gateway.helper.RequestHelper;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.config.conts.ReqCacheKey;
import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.service.ValidService;

import java.util.Arrays;
import java.util.List;

/**
 * @description IP校验过滤器(本过滤器不是全局过滤器，如需使用，需要在配置文件中配置)
 * @author chenyf
 * @date 2019-02-23
 */
public class IPValidGatewayFilterFactory extends AbstractGatewayFilterFactory<IPValidGatewayFilterFactory.Config> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final String IP_VALID_KEY = "ipValidKey";

	@Autowired
	private RequestHelper requestHelper;
	@Autowired
	private ValidService validFailService;

	public IPValidGatewayFilterFactory() {
		super(Config.class);
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(IP_VALID_KEY);
	}

	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			String ip = IPUtil.getIpAddr(request);
			RequestParam requestParam = (RequestParam) exchange.getAttributes().get(ReqCacheKey.CACHE_REQUEST_BODY_OBJECT_KEY);

			boolean isVerifyOk = false;
			String msg = "";

			String expectIp = "";
			String ipValidKey = config.getIpValidKey();
			if(StringUtil.isEmpty(ipValidKey)){
				isVerifyOk = false;
				msg = "Key为空，无法校验请求来源";
			}else{
				try{
					isVerifyOk = requestHelper.ipVerify(ip, ipValidKey, requestParam, new APIParam(requestParam.getSign_type(), requestParam.getVersion()));
					if(! isVerifyOk){
						msg = "非法来源";
					}
				}catch (Throwable e){
					msg = "请求来源校验异常";
					isVerifyOk = false;
					logger.error("IP校验失败异常 RequestParam = {}", JsonUtil.toString(requestParam), e);
				}
			}

			if(! isVerifyOk){
				try{
					Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
					validFailService.afterIpValidFail(route.getId(), ip, expectIp, requestParam);
				}catch(Throwable e){
					logger.error("IP校验失败，IP校验失败处理器有异常 RequestParam = {}", JsonUtil.toString(requestParam), e);
				}
			}

			if(isVerifyOk){
				return chain.filter(exchange);
			}else{
				throw GatewayException.fail(ApiRespCodeEnum.SYS_FORBID.getCode(), msg, GatewayErrorCode.IP_VALID_ERROR);
			}
		};
	}

	public static class Config {
		private String ipValidKey;

		public String getIpValidKey() {
			return ipValidKey;
		}

		public void setIpValidKey(String ipValidKey) {
			this.ipValidKey = ipValidKey;
		}
	}
}
