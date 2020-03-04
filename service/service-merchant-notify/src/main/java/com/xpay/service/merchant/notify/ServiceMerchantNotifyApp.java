package com.xpay.service.merchant.notify;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.xpay.api.base.config.JacksonAutoConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Author: Cmf
 * Date: 2019/10/29
 * Time: 15:20
 * Description: 商户异步通知服务APP
 */
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class, JacksonAutoConfiguration.class})
public class ServiceMerchantNotifyApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceMerchantNotifyApp.class).web(WebApplicationType.NONE).run(args);
    }
}
