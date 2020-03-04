package com.xpay.extend.account;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Author: Cmf
 * Date: 2020.1.8
 * Time: 10:03
 * Description:
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class ExtendAccountApp {
    public static void main(String[] args) {
        SpringApplication.run(ExtendAccountApp.class, args);
    }
}
