package com.xpay.sdktest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class BatchVo implements Serializable {
    private String total_count;
    private BigDecimal total_amount;
    private List<SingleVo> detail_list;

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }

    public List<SingleVo> getDetail_list() {
        return detail_list;
    }

    public void setDetail_list(List<SingleVo> detail_list) {
        this.detail_list = detail_list;
    }
}
