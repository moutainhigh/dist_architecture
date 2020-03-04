package com.xpay.web.pms.vo.trade;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019.12.2
 * Time: 16:27
 * Description:
 */
public class MessageTraceQueryVO implements Serializable {
    private Date pubTimeBegin;
    private Date pubTimeEnd;
    private String trxNo;

    public Date getPubTimeBegin() {
        return pubTimeBegin;
    }

    public void setPubTimeBegin(Date pubTimeBegin) {
        this.pubTimeBegin = pubTimeBegin;
    }

    public Date getPubTimeEnd() {
        return pubTimeEnd;
    }

    public void setPubTimeEnd(Date pubTimeEnd) {
        this.pubTimeEnd = pubTimeEnd;
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }
}
