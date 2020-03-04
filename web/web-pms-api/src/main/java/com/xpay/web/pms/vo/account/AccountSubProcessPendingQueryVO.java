package com.xpay.web.pms.vo.account;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 16:27
 * Description:
 */
public class AccountSubProcessPendingQueryVO implements Serializable {
    private String accountProcessNo;
    private String trxNo;
    private String merchantNo;
    /**
     * {@link com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum}
     */
    private Integer processStage;

    private Date createTimeBegin;
    private Date createTimeEnd;

    public String getAccountProcessNo() {
        return accountProcessNo;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public Integer getProcessStage() {
        return processStage;
    }

    public void setProcessStage(Integer processStage) {
        this.processStage = processStage;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}
