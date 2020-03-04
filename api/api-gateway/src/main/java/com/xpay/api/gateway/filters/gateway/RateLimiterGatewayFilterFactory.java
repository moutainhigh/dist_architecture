package com.xpay.api.gateway.filters.gateway;

import com.xpay.api.gateway.config.conts.GatewayErrorCode;
import com.xpay.api.gateway.exceptions.GatewayException;
import com.xpay.common.statics.enums.common.ApiRespCodeEnum;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description 限流过滤器
 * @author chenyf
 * @date 2019-02-25
 */
public class RateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {
	public static final String KEY_RESOLVER_KEY = "keyResolver";

	private final RateLimiter defaultRateLimiter;
	private final KeyResolver defaultKeyResolver;

	public RateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter,
                                           KeyResolver defaultKeyResolver) {
		super(Config.class);
		this.defaultRateLimiter = defaultRateLimiter;
		this.defaultKeyResolver = defaultKeyResolver;
	}

	@Override
	public GatewayFilter apply(Config config) {
		KeyResolver resolver = (config.keyResolver == null) ? defaultKeyResolver : config.keyResolver;
		RateLimiter<Object> limiter = (config.rateLimiter == null) ? defaultRateLimiter : config.rateLimiter;

		return (exchange, chain) -> {
			Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

			return resolver.resolve(exchange).flatMap(key ->
					limiter.isAllowed(route.getId(), key)
							.flatMap(response -> {

								for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
									exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
								}

								if (response.isAllowed()) {
									return chain.filter(exchange);
								}else{
									//抛出异常，由全局异常处理器来处理响应信息
									throw GatewayException.fail(ApiRespCodeEnum.SYS_FORBID.getCode(), "限流", GatewayErrorCode.RATE_LIMIT_ERROR);
								}
							})
			);
		};
	}

	public KeyResolver getDefaultKeyResolver() {
		return defaultKeyResolver;
	}

	public RateLimiter getDefaultRateLimiter() {
		return defaultRateLimiter;
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(KEY_RESOLVER_KEY);
	}

	public static class Config {
		private KeyResolver keyResolver;
		private RateLimiter rateLimiter;
		private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

		public KeyResolver getKeyResolver() {
			return keyResolver;
		}

		public Config setKeyResolver(KeyResolver keyResolver) {
			this.keyResolver = keyResolver;
			return this;
		}
		public RateLimiter getRateLimiter() {
			return rateLimiter;
		}

		public Config setRateLimiter(RateLimiter rateLimiter) {
			this.rateLimiter = rateLimiter;
			return this;
		}

		public HttpStatus getStatusCode() {
			return statusCode;
		}

		public Config setStatusCode(HttpStatus statusCode) {
			this.statusCode = statusCode;
			return this;
		}
	}

}
