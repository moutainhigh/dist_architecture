package com.xpay.service.notify;

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
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceNotifyApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceNotifyApp.class).web(WebApplicationType.NONE).run(args);
    }
}
