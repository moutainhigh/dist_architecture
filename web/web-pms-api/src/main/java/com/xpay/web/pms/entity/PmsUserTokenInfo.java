package com.xpay.web.pms.entity;

import com.xpay.facade.user.pms.entity.PmsOperator;

import java.io.Serializable;

/**
 * Author: Cmf
 * Date: 2019/10/31
 * Time: 8:59
 * Description:
 */
public class PmsUserTokenInfo implements Serializable {
    private PmsOperator pmsOperator;
    private String sessionUUID;

    public PmsOperator getPmsOperator() {
        return pmsOperator;
    }

    public void setPmsOperator(PmsOperator pmsOperator) {
        this.pmsOperator = pmsOperator;
    }

    public String getSessionUUID() {
        return sessionUUID;
    }

    public void setSessionUUID(String sessionUUID) {
        this.sessionUUID = sessionUUID;
    }
}
