package com.xpay.service.sequence.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leaf")
public class LeafProperties {
    private Segment segment = new Segment();
    private SnowFlake snowflake = new SnowFlake();

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public SnowFlake getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(SnowFlake snowflake) {
        this.snowflake = snowflake;
    }

    public class Segment {
        private Boolean enable = false;
        private String jdbcUrl;
        private String jdbcUsername;
        private String jdbcPassword;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getJdbcUsername() {
            return jdbcUsername;
        }

        public void setJdbcUsername(String jdbcUsername) {
            this.jdbcUsername = jdbcUsername;
        }

        public String getJdbcPassword() {
            return jdbcPassword;
        }

        public void setJdbcPassword(String jdbcPassword) {
            this.jdbcPassword = jdbcPassword;
        }
    }


    public class SnowFlake {
        private Boolean enable = false;
        private String name;
        private String zkAddress;
        private Integer zkPort;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getZkAddress() {
            return zkAddress;
        }

        public void setZkAddress(String zkAddress) {
            this.zkAddress = zkAddress;
        }

        public Integer getZkPort() {
            return zkPort;
        }

        public void setZkPort(Integer zkPort) {
            this.zkPort = zkPort;
        }
    }
}
