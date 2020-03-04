package com.xpay.sdktest;

import java.util.List;

public class BatchRespVo {
    private String order_status;
    private String total_count;
    private List<SingleRespVo> single_List;

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public List<SingleRespVo> getSingle_List() {
        return single_List;
    }

    public void setSingle_List(List<SingleRespVo> single_List) {
        this.single_List = single_List;
    }
}
