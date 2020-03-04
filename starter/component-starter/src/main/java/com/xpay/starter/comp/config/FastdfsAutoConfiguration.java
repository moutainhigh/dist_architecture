package com.xpay.starter.comp.config;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.starter.comp.component.FastdfsClient;
import com.xpay.starter.comp.properties.FastdfsProperties;
import com.xpay.csource.fastdfs.ClientGlobal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Author: Cmf
 * Date: 2019/11/13
 * Time: 19:13
 * Description:
 */
@Configuration
@ConditionalOnClass(ClientGlobal.class)
@EnableConfigurationProperties(FastdfsProperties.class)
public class FastdfsAutoConfiguration {
    @Autowired
    private FastdfsProperties fastdfsProperties;

    @Bean
    public FastdfsClient fastdfsClient() {
        initClientGlobal();//1.先初始化环境变量
        return new FastdfsClient();//2.再创建客户端对象
    }

    private void initClientGlobal() {
        try{
            Properties properties = new Properties();
            properties.setProperty(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, fastdfsProperties.getConnectTimeoutInSeconds() + "");
            properties.setProperty(ClientGlobal.PROP_KEY_TRACKER_SERVERS, fastdfsProperties.getTrackerServers());
            properties.setProperty(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, fastdfsProperties.getNetworkTimeoutInSeconds() + "");
            properties.setProperty(ClientGlobal.PROP_KEY_CHARSET, fastdfsProperties.getCharset());
            properties.setProperty(ClientGlobal.PROP_KEY_HTTP_ANTI_STEAL_TOKEN, fastdfsProperties.isHttpAntiStealToken() + "");
            properties.setProperty(ClientGlobal.PROP_KEY_HTTP_SECRET_KEY, fastdfsProperties.getHttpSecretKey());
            ClientGlobal.initByProperties(properties);
        }catch(Exception e){
            throw new BizException("初始化fastdfs异常 ", e);
        }
    }
}
