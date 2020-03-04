package com.xpay.extend.account.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Author: Cmf
 * Date: 2020.1.19
 * Time: 15:49
 * Description: 应用本身需要的相关配置，请在应用本身的配置文件中进行配置
 */

@SpringBootConfiguration
@ConfigurationProperties(prefix = "app.config")
public class AppConfigProperties {
    private String globalLockMailReceivers;     //全局锁异常邮件接收方

    public String getGlobalLockMailReceivers() {
        return globalLockMailReceivers;
    }

    public void setGlobalLockMailReceivers(String globalLockMailReceivers) {
        this.globalLockMailReceivers = globalLockMailReceivers;
    }
}
