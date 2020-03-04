package com.xpay.starter.comp.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.xpay.starter.comp.component.ESClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@AutoConfigureAfter(RestClientAutoConfiguration.class)
@ConditionalOnClass(RestHighLevelClient.class)
@Configuration
public class EsAutoConfiguration {

    @ConditionalOnBean(RestHighLevelClient.class)
    @Bean(destroyMethod = "destroy")
    public ESClient esClient(RestHighLevelClient restHighLevelClient){
        return new ESClient(restHighLevelClient, esMappingCache());
    }

    /**
     * guava 本地缓存
     *
     * @return
     */
    @Bean
    public Cache<String, Map<String, String>> esMappingCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)//过期时间
                .maximumSize(10000)
                .initialCapacity(50)
                .concurrencyLevel(10)
                .build();
    }
}
