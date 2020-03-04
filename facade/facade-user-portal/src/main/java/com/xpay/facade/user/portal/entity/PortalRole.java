package com.xpay.facade.user.portal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:15
 * Description: 商户操作员权限管理-角色
 */
public class PortalRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer version = 0;
    private Date createTime = new Date();
    private String merchantNo;  //商户编码
    private Integer roleType; // 角色类型（1:商户管理员角色，2:普通操作员角色）
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

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
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
