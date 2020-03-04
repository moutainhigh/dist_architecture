package com.xpay.facade.user.pms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:17
 * Description: 权限管理-角色,操作员关联表
 */
public class PmsRoleOperator implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer version = 0;
    private Date createTime = new Date();
    private Long roleId;// 角色ID
    private Long operatorId;// /操作员ID

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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
