package com.xpay.web.pms.vo.account;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 16:27
 * Description:
 */
public class AccountTransitQueryVO implements Serializable {
    private String merchantNo;
    private Integer status;
    private Date createTimeBegin;
    private Date createTimeEnd;

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
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
