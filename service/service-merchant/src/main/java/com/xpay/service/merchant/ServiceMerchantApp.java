package com.xpay.service.merchant;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Author: Cmf
 * Date: 2020.2.13
 * Time: 15:44
 * Description: 商户管理服务APP
 */
@SpringBootApplication
public class ServiceMerchantApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceMerchantApp.class).web(WebApplicationType.NONE).run(args);
    }
}
