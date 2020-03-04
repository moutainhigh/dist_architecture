package com.xpay.api.gateway.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xpay.api.gateway.helper.RequestHelper;
import com.xpay.api.gateway.service.MchServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.validation.Validator;
import com.xpay.api.gateway.filters.gateway.IPValidGatewayFilterFactory;
import com.xpay.api.gateway.filters.gateway.RateLimiterGatewayFilterFactory;
import com.xpay.api.gateway.ratelimit.PathKeyResolver;
import com.xpay.api.gateway.ratelimit.PathRedisRateLimiter;
import com.xpay.api.gateway.ratelimit.SimpleRateLimiter;
import com.xpay.api.gateway.service.ValidServiceImpl;
import com.xpay.api.base.service.MchService;
import com.xpay.api.base.service.ValidService;
import com.xpay.api.base.params.MerchantInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootConfiguration
public class GatewayConfig {

    @Bean
    public IPValidGatewayFilterFactory ipValidGatewayFilterFactory(){
        return new IPValidGatewayFilterFactory();
    }

    @Primary
    @Bean
    public PathRedisRateLimiter pathRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                                     @Qualifier(PathRedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
                                                     Validator validator) {
        return new PathRedisRateLimiter(redisTemplate, redisScript, validator);
    }

    @Bean
    public RateLimiterGatewayFilterFactory rateLimiterGatewayFilterFactory(PathRedisRateLimiter pathRedisRateLimiter, PathKeyResolver pathKeyResolver) {
        return new RateLimiterGatewayFilterFactory(pathRedisRateLimiter, pathKeyResolver);
    }

    @Bean("pathKeyResolver")
    public PathKeyResolver pathKeyResolver(){
        return new PathKeyResolver();
    }

    @Bean
    public SimpleRateLimiter simpleRateLimiter(RedisTemplate<String, String> redisTemplate, RedisScript<Long> simpleRateLimiterScript){
        return new SimpleRateLimiter(redisTemplate, simpleRateLimiterScript);
    }

    @Bean("simpleRateLimiterScript")
    public RedisScript simpleRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/simple_rate_limiter.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean
    public ValidService validFailService(){
        return new ValidServiceImpl();
    }

    @Bean
    public RequestHelper requestHelper(MchService mchService){
        return new RequestHelper(mchService);
    }

    /**
     * guava 本地缓存
     * @return
     */
    @Bean
    public Cache<String, MerchantInfo> cache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)//过期时间
                .maximumSize(10000)
                .initialCapacity(50)
                .concurrencyLevel(10)
                .build();
    }

    @Bean
    public MchService mchService(){
        return new MchServiceImpl(cache());
    }
}
