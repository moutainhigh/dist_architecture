package com.xpay.service.config;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServiceConfigApp {

    public static void main(String[] args){
        new SpringApplicationBuilder(ServiceConfigApp.class).web(WebApplicationType.NONE).run(args);
    }
}