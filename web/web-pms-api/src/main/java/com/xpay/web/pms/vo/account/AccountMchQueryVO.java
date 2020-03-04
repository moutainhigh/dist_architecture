package com.xpay.web.pms.vo.account;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luobinzhao
 * @date 2020/1/16 9:38
 */
public class AccountMchQueryVO implements Serializable {
    private static final long serialVersionUID = -5546678715300263074L;
    private String merchantNo;
    private String merchantType;
    private Integer status;
    private Date createTimeBegin;
    private Date createTimeEnd;

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
