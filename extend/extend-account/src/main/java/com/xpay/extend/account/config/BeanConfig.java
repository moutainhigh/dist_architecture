package com.xpay.extend.account.config;

import com.xpay.starter.comp.component.TaskExecutorPool;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Author: Cmf
 * Date: 2020.1.19
 * Time: 15:57
 * Description: 用于配置需要手工生成的Bean
 */
@SpringBootConfiguration
public class BeanConfig {

    @Bean
    public TaskExecutorPool scheduleTaskAddPool() {
        TaskExecutorPool pool = new TaskExecutorPool();
        pool.setThreadNamePrefix("scheduleTaskAddPool-");
        //线程池维护线程的最少数量
        pool.setCorePoolSize(5);
        //线程池所使用的缓冲队列
        pool.setQueueCapacity(50);
        //线程池维护线程的最大数量
        pool.setMaxPoolSize(10);
        //线程池维护线程所允许的空闲时间(即额外的线程等待多久之后会被自动销毁)
        pool.setKeepAliveSeconds(300);
        pool.setRejectedExecutionHandler(null);
        pool.initialize();
        return pool;
    }


    @Bean
    public TaskExecutorPool scheduleTaskExecutePool() {
        TaskExecutorPool pool = new TaskExecutorPool();
        pool.setThreadNamePrefix("scheduleTaskExecutePool-");
        //线程池维护线程的最少数量
        pool.setCorePoolSize(15);
        //线程池所使用的缓冲队列
        pool.setQueueCapacity(500);
        //线程池维护线程的最大数量
        pool.setMaxPoolSize(25);
        //线程池维护线程所允许的空闲时间(即额外的线程等待多久之后会被自动销毁)
        pool.setKeepAliveSeconds(300);
        pool.initialize();
        return pool;
    }
}
