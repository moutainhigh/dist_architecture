package com.xpay.web.pms.vo.system;

/**
 * Author: Cmf
 * Date: 2020.1.15
 * Time: 11:21
 * Description:
 */
public class GlobalLockQueryVO {
    private String resourceId;
    private String clientId;
    private Integer resourceStatus;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(Integer resourceStatus) {
        this.resourceStatus = resourceStatus;
    }
}
