package com.xpay.service.accountsub;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServiceAccountSubApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceAccountSubApp.class).web(WebApplicationType.NONE).run(args);
    }
}
