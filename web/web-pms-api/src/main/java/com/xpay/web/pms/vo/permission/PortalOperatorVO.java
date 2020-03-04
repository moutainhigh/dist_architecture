package com.xpay.web.pms.vo.permission;

import com.xpay.facade.user.portal.entity.PortalOperator;

/**
 * 商户操作员查询VO
 */
public class PortalOperatorVO {

    private Long id;
    /**
     * 登录名
     */
    private String loginName;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobileNo;

    /**
     * email
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 操作员类型
     */
    private Integer type;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 集团编码
     */
    private String orgNo;

    /**
     * 描述
     */
    private String remark;

    /**
     * 角色
     */
    private Long[] roles;

    /**
     * 角色名称
     */
    private String roleNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long[] getRoles() {
        return roles;
    }

    public void setRoles(Long[] roles) {
        this.roles = roles;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public PortalOperatorVO() {
    }

    public PortalOperatorVO(PortalOperator portalOperator) {
        this.id = portalOperator.getId();
        this.loginName = portalOperator.getLoginName();
        this.realName = portalOperator.getRealName();
        this.mobileNo = portalOperator.getMobileNo();
        this.email = portalOperator.getEmail();
        this.status = portalOperator.getStatus();
        this.type = portalOperator.getType();
        this.merchantNo = portalOperator.getMerchantNo();
        this.orgNo = portalOperator.getOrgNo();
        this.remark = portalOperator.getRemark();
    }
}
