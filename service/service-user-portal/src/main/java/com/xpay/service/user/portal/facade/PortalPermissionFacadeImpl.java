package com.xpay.service.user.portal.facade;


import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalFunction;
import com.xpay.facade.user.portal.entity.PortalRole;
import com.xpay.facade.user.portal.service.PortalPermissionFacade;
import com.xpay.service.user.portal.core.biz.PortalPermissionBiz;
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
public class PortalPermissionFacadeImpl implements PortalPermissionFacade {
    @Autowired
    private PortalPermissionBiz portalPermissionBiz;

    //<editor-fold desc="功能点管理">
    @Override
    public void createFunction(PortalFunction portalFunction) {
        portalPermissionBiz.createFunction(portalFunction);
    }

    @Override
    public void deleteFunctionById(Long functionId) {
        portalPermissionBiz.deleteFunctionById(functionId);
    }

    @Override
    public void updateFunction(PortalFunction portalFunction) {
        portalPermissionBiz.updateFunction(portalFunction);
    }

    @Override
    public List<PortalFunction> listFunctionByOperatorId(Long operatorId) {
        return portalPermissionBiz.listFunctionByOperatorId(operatorId);
    }

    @Override
    public List<PortalFunction> listAllFunction() {
        return portalPermissionBiz.listAllFunction();
    }

    @Override
    public List<Long> listFunctionIdsByRoleId(Long roleId) {
        return portalPermissionBiz.listFunctionIdsByRoleId(roleId);
    }

    @Override
    public List<PortalFunction> listFunctionByRoleId(Long roleId) {
        return portalPermissionBiz.listFunctionByRoleId(roleId);
    }

    @Override
    public List<PortalFunction> listFunctionByParentId(Long parentId) {
        return portalPermissionBiz.listFunctionByParentId(parentId);
    }

    @Override
    public PortalFunction getFunctionById(Long id) {
        return portalPermissionBiz.getFunctionById(id);
    }

    @Override
    public PortalFunction getFunctionWithParentInfo(Long id) {
        return portalPermissionBiz.getFunctionWithParentInfo(id);
    }
    //</editor-fold>


    //<editor-fold desc="角色管理">
    @Override
    public void createRole(PortalRole portalRole) {
        portalPermissionBiz.createRole(portalRole);
    }

    @Override
    public void deleteRoleById(Long id) {
        portalPermissionBiz.deleteRoleById(id);

    }

    @Override
    public void updateRole(PortalRole portalRole) {
        portalPermissionBiz.updateRole(portalRole);
    }

    @Override
    public List<PortalRole> listAllRoles() {
        return portalPermissionBiz.listAllRoles();
    }

    @Override
    public List<PortalRole> listAllAdminRoles() {
        return portalPermissionBiz.listAllAdminRoles();
    }

    @Override
    public List<PortalRole> listRoleByMerchantNo(String merchantNo) {
        return portalPermissionBiz.listRoleByMerchantNo(merchantNo);
    }

    @Override
    public PageResult<List<PortalRole>> listRolePage(Map<String, Object> paramMap, PageParam pageParam) {
        return portalPermissionBiz.listRolePage(paramMap, pageParam);
    }

    @Override
    public List<PortalRole> listRolesByOperatorId(Long operatorId) {
        return portalPermissionBiz.listRolesByOperatorId(operatorId);
    }


    @Override
    public PortalRole getRoleById(Long id) {
        return portalPermissionBiz.getRoleById(id);
    }

    @Override
    public PortalRole getRoleByName(String roleName) {
        return portalPermissionBiz.getRoleByName(roleName);
    }


    @Override
    public void assignPermission(Long roleId, List<Long> functionIds) {
        portalPermissionBiz.assignPermission(roleId, functionIds);
    }
    //</editor-fold>

}
