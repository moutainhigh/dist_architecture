package com.xpay.web.pms.vo.account;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 16:27
 * Description:
 */
public class AccountSubProcessDetailQueryVO implements Serializable {
    private String accountProcessNo; //账务处理流水号
    private String merchantNo;      //商户编号
    private String trxNo;           //平台流水号
    private Integer processType;    //账务处理类型 com.xpay.common.statics.enums.account.AccountProcessType
    private Date createTimeBegin;   //记账时间-开始
    private Date createTimeEnd;     //记账时间-结束

    public String getAccountProcessNo() {
        return accountProcessNo;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
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
