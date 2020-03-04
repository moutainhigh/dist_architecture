package com.xpay.web.pms.vo.permission;

import com.xpay.common.statics.result.PageParam;

/**
 * Author: Cmf
 * Date: 2020.1.20
 * Time: 15:43
 * Description: 操作员查询VO
 */
public class PmsOperatorQueryVO extends PageParam {

    /**
     * 登录名
     */
    private String loginName;
    /**
     * 姓名
     */
    private String realName;
    /**
     * 状态
     */
    private Integer status;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
