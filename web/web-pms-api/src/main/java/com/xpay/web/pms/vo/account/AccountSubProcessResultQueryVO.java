package com.xpay.web.pms.vo.account;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 16:27
 * Description:
 */
public class AccountSubProcessResultQueryVO implements Serializable {
    private String accountProcessNo; //账务处理流水号
    private String trxNo;           //平台流水号
    private String merchantNo;      //商户编号
    private Integer callbackStage;  //发送阶段 com.xpay.common.statics.enums.account.AccountProcessResultCallbackStage
    private Integer auditStage;     //审核阶段com.xpay.common.statics.enums.account.AccountProcessResultAuditStage
    private Integer processResult;  //处理结果 com.xpay.common.statics.constants.common.PublicStatus 1成功，-1失败
    private Date createTimeBegin;   //账务处理结果创建时间-开始
    private Date createTimeEnd;     //账务处理结果创建时间-结束
    private Integer isFromAsync;        //异步账务com.xpay.common.statics.constants.common.PublicStatus 1是，-1否

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

    public Integer getCallbackStage() {
        return callbackStage;
    }

    public void setCallbackStage(Integer callbackStage) {
        this.callbackStage = callbackStage;
    }

    public Integer getAuditStage() {
        return auditStage;
    }

    public void setAuditStage(Integer auditStage) {
        this.auditStage = auditStage;
    }

    public Integer getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Integer processResult) {
        this.processResult = processResult;
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

    public Integer getIsFromAsync() {
        return isFromAsync;
    }

    public void setIsFromAsync(Integer isFromAsync) {
        this.isFromAsync = isFromAsync;
    }
}
