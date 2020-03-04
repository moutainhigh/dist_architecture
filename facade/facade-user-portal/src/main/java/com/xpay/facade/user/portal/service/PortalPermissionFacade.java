package com.xpay.facade.user.portal.service;


import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalFunction;
import com.xpay.facade.user.portal.entity.PortalRole;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:31
 * Description:管理菜单和权限
 */
public interface PortalPermissionFacade {

    //<editor-fold desc="功能点管理">
    void createFunction(PortalFunction portalFunction);

    /**
     * 删除Function以及其子Function,同时也会删除与这些function相关的role_function映射关联
     * 若存在菜单类的子Function，该方法会抛出异常
     *
     * @param functionId .
     */
    void deleteFunctionById(Long functionId);

    void updateFunction(PortalFunction portalFunction);

    List<PortalFunction> listFunctionByOperatorId(Long operatorId);

    List<PortalFunction> listAllFunction();

    List<Long> listFunctionIdsByRoleId(Long roleId);

    List<PortalFunction> listFunctionByRoleId(Long roleId);

    List<PortalFunction> listFunctionByParentId(Long id);

    PortalFunction getFunctionById(Long id);

    PortalFunction getFunctionWithParentInfo(Long id);
    //</editor-fold>


    //<editor-fold desc="角色管理">
    void createRole(PortalRole portalRole);

    void deleteRoleById(Long id);

    void updateRole(PortalRole portalRole);

    List<PortalRole> listAllRoles();

    List<PortalRole> listAllAdminRoles();

    List<PortalRole> listRoleByMerchantNo(String merchantNo);

    PageResult<List<PortalRole>> listRolePage(Map<String, Object> paramMap, PageParam pageParam);

    List<PortalRole> listRolesByOperatorId(Long operatorId);

    PortalRole getRoleById(Long id);

    PortalRole getRoleByName(String roleName);


    void assignPermission(Long roleId, List<Long> functionIds);

    //</editor-fold>


}
