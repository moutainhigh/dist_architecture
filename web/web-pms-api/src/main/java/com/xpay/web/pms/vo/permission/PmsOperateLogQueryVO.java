package com.xpay.web.pms.vo.permission;

import java.util.Date;

/**
 * Author: Cmf
 * Date: 2020.1.20
 * Time: 19:00
 * Description:运营后台操作日志查询VO
 */
public class PmsOperateLogQueryVO {
    private String operatorLoginName;
    private Date createTimeBegin;
    private Date createTimeEnd;
    /**
     * {@link com.xpay.common.statics.enums.user.pms.PmsOperateLogTypeEnum}
     */
    private Integer operateType;

    public String getOperatorLoginName() {
        return operatorLoginName;
    }

    public void setOperatorLoginName(String operatorLoginName) {
        this.operatorLoginName = operatorLoginName;
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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }
}
