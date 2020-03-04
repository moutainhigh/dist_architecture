package com.xpay.service.lock;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServiceGlobalLockApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceGlobalLockApp.class).web(WebApplicationType.NONE).run(args);
    }
}
