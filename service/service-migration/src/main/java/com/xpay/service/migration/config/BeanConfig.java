package com.xpay.service.migration.config;

import com.xpay.starter.comp.component.TaskExecutorPool;
import com.xpay.starter.generic.hepler.GlobalLockHelper;
import com.xpay.starter.generic.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 用于配置需要手工生成的Bean
 */
@SpringBootConfiguration
public class BeanConfig {

    @Bean
    public GlobalLockHelper globalLockHelper(@Autowired BaseService baseService) {
        return new GlobalLockHelper(baseService);
    }

    @Bean
    public TaskExecutorPool taskExecutorPool() {
        TaskExecutorPool executor = new TaskExecutorPool();
        executor.setThreadNamePrefix("migrate-");
        //线程池所使用的缓冲队列
        executor.setQueueCapacity(1000);
        //线程池维护线程的最少数量
        executor.setCorePoolSize(10);
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(50);
        //线程池维护线程所允许的空闲时间(即额外的线程等待多久之后会被自动销毁)
        executor.setKeepAliveSeconds(5);
        executor.initialize();
        return executor;
    }
}
