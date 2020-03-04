package com.xpay.service.rocketmq.manage.config;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Author: Cmf
 * Date: 2020.1.16
 * Time: 14:50
 * Description:
 */
@SpringBootConfiguration
public class BeanConfig {
    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Bean(destroyMethod = "shutdown")
    public DefaultMQAdminExt defaultMQAdminExt() throws MQClientException {
        DefaultMQAdminExt mqAdminExt = new DefaultMQAdminExt();
        mqAdminExt.setNamesrvAddr(rocketMQProperties.getNameServer());
        mqAdminExt.start();
        return mqAdminExt;
    }


    @Bean(destroyMethod = "shutdown")
    public DefaultMQPullConsumer defaultMQPullConsumer() throws MQClientException {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(MixAll.TOOLS_CONSUMER_GROUP, (RPCHook) null);
        consumer.setNamesrvAddr(rocketMQProperties.getNameServer());
        consumer.start();
        return consumer;
    }
}
