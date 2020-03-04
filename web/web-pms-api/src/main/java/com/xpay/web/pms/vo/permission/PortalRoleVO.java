package com.xpay.web.pms.vo.permission;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 角色VO
 *
 * @author linguangsheng
 * @date 2019/10/31
 */
public class PortalRoleVO {

    /**
     * 角色名称
     */
    @NotEmpty(message = "角色名称不能为空")
    @Size(min = 3, max = 90, message = "角色名称在3到90个字符")
    private String roleName;

    /**
     * 描述
     */
    private String remark;

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

    @Override
    public String toString() {
        return "PmsRoleVO{" +
                "roleName='" + roleName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
