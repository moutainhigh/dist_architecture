package com.xpay.sdktest;

import java.io.Serializable;
import java.math.BigDecimal;

public class SingleVo implements Serializable {
    private String product_name;
    private BigDecimal product_amount;
    private String count;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public BigDecimal getProduct_amount() {
        return product_amount;
    }

    public void setProduct_amount(BigDecimal product_amount) {
        this.product_amount = product_amount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
