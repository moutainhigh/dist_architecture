/**
 *
 */
package com.xpay.facade.user.pms.entity;

import java.io.Serializable;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:16
 * Description: 角色功能关联表
 */
public class PmsRoleFunction implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long roleId; // 角色ID
    private Long functionId; // 功能ID

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
}
