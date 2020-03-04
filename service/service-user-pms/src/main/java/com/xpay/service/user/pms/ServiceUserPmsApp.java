package com.xpay.service.user.pms;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Author: Cmf
 * Date: 2019/10/29
 * Time: 15:20
 * Description: 运营后台操作员服务
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServiceUserPmsApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceUserPmsApp.class).web(WebApplicationType.NONE).run(args);
    }
}
