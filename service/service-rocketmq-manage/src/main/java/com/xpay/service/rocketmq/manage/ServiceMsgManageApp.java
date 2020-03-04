package com.xpay.service.rocketmq.manage;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Author: Cmf
 * Date: 2019/10/29
 * Time: 15:20
 * Description: 业务通知服务app
 */
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class})
public class ServiceMsgManageApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceMsgManageApp.class).web(WebApplicationType.NONE).run(args);
    }
}
