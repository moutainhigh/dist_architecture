package com.xpay.demo.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WebBackendApp {
    public static void main(String[] args){
        new SpringApplicationBuilder(WebBackendApp.class).run(args);
    }
}
