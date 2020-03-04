package com.xpay.service.timer.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SpringBootConfiguration
@ConfigurationProperties(prefix = "timer")
public class TimerConfig {
    /**
     * 实例的命名空间，可用以区分不同的环境、IDC等，适合有'蓝绿发布'的场景
     */
    private String namespace = "default";
    /**
     * 命名空间的中文名称
     */
    private String nameCn = "默认空间";
    /**
     * 实例状态检查间隔，不建议设置过长，因为会导致实例状态设置后太久才生效，也不建议设置过短，因为这会加重数据库负担
     */
    private int stageCheckMills = 5000;
    /**
     * 是否需要记录操作日志
     */
    private boolean opLogEnable = true;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public int getStageCheckMills() {
        return stageCheckMills;
    }

    public void setStageCheckMills(int stageCheckMills) {
        this.stageCheckMills = stageCheckMills;
    }

    public boolean getOpLogEnable() {
        return opLogEnable;
    }

    public void setOpLogEnable(boolean opLogEnable) {
        this.opLogEnable = opLogEnable;
    }
}
