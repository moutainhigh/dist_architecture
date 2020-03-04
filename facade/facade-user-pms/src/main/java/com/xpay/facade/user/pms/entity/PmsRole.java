package com.xpay.facade.user.pms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:15
 * Description: 权限管理-角色
 */
public class PmsRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer version = 0;
    private Date createTime = new Date();
    private String roleName; // 角色名称
    private String remark; // 描述

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
