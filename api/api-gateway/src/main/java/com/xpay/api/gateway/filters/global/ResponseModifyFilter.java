package com.xpay.api.gateway.filters.global;

import com.xpay.api.base.constants.HttpHeaderKey;
import com.xpay.api.gateway.filters.cache.CachedBodyOutputMessage;
import com.xpay.api.gateway.helper.RequestHelper;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import com.xpay.api.gateway.config.conts.FilterOrder;
import com.xpay.api.base.params.APIParam;
import com.xpay.api.base.params.RequestParam;
import com.xpay.api.base.params.ResponseParam;
import com.xpay.api.base.utils.ResponseUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * @description 响应体修改，包括：给响应体加签名 等
 * @author chenyf
 * @date 2019-02-23
 */
@Component
public class ResponseModifyFilter extends AbstractGlobalFilter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	RequestHelper requestHelper;

	/**
	 * 设置当前过滤器的执行顺序：本过滤器在全局过滤器中的顺序为倒数第1个，在响应给用户之前给响应参数加上签名
	 * @return
	 */
	@Override
	public int getOrder() {
		return FilterOrder.RESPONSE_MODIFY_FILTER;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);

				ClientResponse clientResponse = ClientResponse.create(exchange.getResponse().getStatusCode())
						.headers((headers) ->
						{ headers.putAll(httpHeaders); })
						.body(Flux.from(body))
						.build();

				Mono modifiedBody = clientResponse.bodyToMono(String.class)
						.flatMap((originalBody) -> {
							return Mono.just(buildNewResponseBody(exchange, originalBody, httpHeaders)
							);
						});

				BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
				CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, exchange.getResponse().getHeaders());
				return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
					Flux<DataBuffer> messageBody = outputMessage.getBody();
					HttpHeaders headers = this.getDelegate().getHeaders();
					if (!headers.containsKey("Transfer-Encoding")) {
						messageBody = messageBody.doOnNext((data) -> {
							headers.setContentLength((long)data.readableByteCount());
						});
					}

					return this.getDelegate().writeWith(messageBody);
				}));
			}

			public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
				return this.writeWith(Flux.from(body).flatMapSequential((p) -> {
					return p;
				}));
			}
		};
		return chain.filter(exchange.mutate().response(responseDecorator).build());
	}

	public String buildNewResponseBody(ServerWebExchange exchange, String originalBody, HttpHeaders httpHeaders){
		String respOriginalBody = httpHeaders.getFirst(HttpHeaderKey.RESP_ORIGINAL_BODY_KEY);
		//关闭对报文的加签，目前暂时网关支付返回页面需要关闭
		if (StringUtil.isNotEmpty(respOriginalBody) && "true".equals(respOriginalBody)) {
			//删除自定义的header，不能展示给页面或商户
			httpHeaders.remove(HttpHeaderKey.RESP_ORIGINAL_BODY_KEY);
			return originalBody;
		}

		RequestParam requestParam = (RequestParam) exchange.getAttributes().get(CACHE_REQUEST_BODY_OBJECT_KEY);
		ResponseParam responseParam = JsonUtil.toBean(originalBody, ResponseParam.class);
		if(responseParam == null){
			return originalBody;//相应体为空，则原样返回
		}

		try{
			if(StringUtil.isEmpty(responseParam.getMch_no())){
				responseParam.setMch_no(requestParam==null ? "" : requestParam.getMch_no());
			}
			if(StringUtil.isEmpty(responseParam.getSign_type())){
				responseParam.setSign_type(requestParam==null ? "" : requestParam.getSign_type());
			}
			ResponseUtil.fillAcceptUnknownIfEmpty(responseParam);
			//添加签名
			requestHelper.signAndEncrypt(responseParam, new APIParam(requestParam.getSign_type(), requestParam.getVersion()));
		}catch (Throwable e){
			logger.error("给响应信息添加签名时异常 RequestParam = {} ResponseParam = {}", JsonUtil.toString(requestParam), JsonUtil.toString(responseParam), e);
		}

		responseParam.setSign(responseParam.getSign() == null ? "" : responseParam.getSign());
		String str = JsonUtil.toString(responseParam);//ResponseParam.data中的数据会被加上反斜杠转义符
		return str;
	}
}
