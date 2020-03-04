package com.xpay.service.accounttransit;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServiceAccountTransitApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceAccountTransitApp.class).web(WebApplicationType.NONE).run(args);
    }
}
