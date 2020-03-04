package com.xpay.api.gateway.config;

import org.springframework.boot.SpringBootConfiguration;

@SpringBootConfiguration
public class PropertiesConfig {
    private String ipBlackListPattern = "";
    private String offBuss = "";


    public String getIpBlackListPattern() {
        return ipBlackListPattern;
    }

    public void setIpBlackListPattern(String ipBlackListPattern) {
        this.ipBlackListPattern = ipBlackListPattern;
    }

    public String getOffBuss() {
        return offBuss;
    }

    public void setOffBuss(String offBuss) {
        this.offBuss = offBuss;
    }
}
