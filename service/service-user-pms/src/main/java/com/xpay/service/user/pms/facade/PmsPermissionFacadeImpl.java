package com.xpay.service.user.pms.facade;


import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsFunction;
import com.xpay.facade.user.pms.entity.PmsRole;
import com.xpay.facade.user.pms.service.PmsPermissionFacade;
import com.xpay.service.user.pms.core.biz.PmsPermissionBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:42
 * Description:
 */
@Service
public class PmsPermissionFacadeImpl implements PmsPermissionFacade {
    @Autowired
    private PmsPermissionBiz pmsPermissionBiz;

    //<editor-fold desc="功能点管理">
    @Override
    public void createFunction(PmsFunction pmsFunction) {
        pmsPermissionBiz.createFunction(pmsFunction);
    }

    @Override
    public void deleteFunctionById(Long functionId) {
        pmsPermissionBiz.deleteFunctionById(functionId);
    }

    @Override
    public void updateFunction(PmsFunction pmsFunction) {
        pmsPermissionBiz.updateFunction(pmsFunction);
    }

    @Override
    public List<PmsFunction> listFunctionByOperatorId(Long operatorId) {
        return pmsPermissionBiz.listFunctionByOperatorId(operatorId);
    }

    @Override
    public List<PmsFunction> listAllFunction() {
        return pmsPermissionBiz.listAllFunction();
    }

    @Override
    public List<Long> listFunctionIdsByRoleId(Long roleId) {
        return pmsPermissionBiz.listFunctionIdsByRoleId(roleId);
    }

    @Override
    public List<PmsFunction> listFunctionByRoleId(Long roleId) {
        return pmsPermissionBiz.listFunctionByRoleId(roleId);
    }

    @Override
    public List<PmsFunction> listFunctionByParentId(Long parentId) {
        return pmsPermissionBiz.listFunctionByParentId(parentId);
    }

    @Override
    public PmsFunction getFunctionById(Long id) {
        return pmsPermissionBiz.getFunctionById(id);
    }

    @Override
    public PmsFunction getFunctionWithParentInfo(Long id) {
        return pmsPermissionBiz.getFunctionWithParentInfo(id);
    }
    //</editor-fold>


    //<editor-fold desc="角色管理">
    @Override
    public void createRole(PmsRole pmsRole) {
        pmsPermissionBiz.createRole(pmsRole);
    }

    @Override
    public void deleteRoleById(Long id) {
        pmsPermissionBiz.deleteRoleById(id);
    }

    @Override
    public void updateRole(PmsRole pmsRole) {
        pmsPermissionBiz.updateRole(pmsRole);
    }

    @Override
    public List<PmsRole> listAllRoles() {
        return pmsPermissionBiz.listAllRoles();
    }

    @Override
    public PageResult<List<PmsRole>> listRolePage(Map<String, Object> paramMap, PageParam pageParam) {
        return pmsPermissionBiz.listRolePage(paramMap, pageParam);
    }

    @Override
    public List<PmsRole> listRolesByOperatorId(Long operatorId) {
        return pmsPermissionBiz.listRolesByOperatorId(operatorId);
    }

    @Override
    public PmsRole getRoleById(Long id) {
        return pmsPermissionBiz.getRoleById(id);
    }

    @Override
    public PmsRole getRoleByName(String roleName) {
        return pmsPermissionBiz.getRoleByName(roleName);
    }

    @Override
    public void assignPermission(Long roleId, List<Long> functionIds) {
        pmsPermissionBiz.assignPermission(roleId, functionIds);
    }
    //</editor-fold>

}
