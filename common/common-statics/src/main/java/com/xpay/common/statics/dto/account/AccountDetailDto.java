package com.xpay.common.statics.dto.account;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccountDetailDto implements Serializable {
    private static final long serialVersionUID = -1636546569587411315L;

    private String userNo;
    private String requestNo;
    private Integer processType;
    /**
     * 变动金额
     */
    private BigDecimal alterAmount;
    /**
     * 已结算变动金额
     */
    private BigDecimal alterSettleAmount;
    /**
     * 可垫资变动金额
     */
    private BigDecimal alterAdvanceAmount;
    /**
     * 留存金额变动金额
     */
    private BigDecimal alterRetainAmount;
    /**
     * 清算版本号
     */
    private Integer clearVersion;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public BigDecimal getAlterAmount() {
        return alterAmount;
    }

    public void setAlterAmount(BigDecimal alterAmount) {
        this.alterAmount = alterAmount;
    }

    public BigDecimal getAlterSettleAmount() {
        return alterSettleAmount;
    }

    public void setAlterSettleAmount(BigDecimal alterSettleAmount) {
        this.alterSettleAmount = alterSettleAmount;
    }

    public BigDecimal getAlterAdvanceAmount() {
        return alterAdvanceAmount;
    }

    public void setAlterAdvanceAmount(BigDecimal alterAdvanceAmount) {
        this.alterAdvanceAmount = alterAdvanceAmount;
    }

    public BigDecimal getAlterRetainAmount() {
        return alterRetainAmount;
    }

    public void setAlterRetainAmount(BigDecimal alterRetainAmount) {
        this.alterRetainAmount = alterRetainAmount;
    }

    public Integer getClearVersion() {
        return clearVersion;
    }

    public void setClearVersion(Integer clearVersion) {
        this.clearVersion = clearVersion;
    }
}
