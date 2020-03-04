package com.xpay.inner.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class innerGatewayApp {
    public static void main(String[] args) {
        SpringApplication.run(innerGatewayApp.class, args);
    }
}
