package com.xpay.facade.user.pms.service;


import com.xpay.common.statics.exception.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsFunction;
import com.xpay.facade.user.pms.entity.PmsRole;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:31
 * Description:管理菜单和权限
 */
public interface PmsPermissionFacade {

    //<editor-fold desc="功能点管理">
    void createFunction(PmsFunction pmsFunction);

    /**
     * 删除Function以及其子Function,同时也会删除与这些function相关的role_function映射关联
     * 若存在菜单类的子Function，该方法会抛出异常
     *
     * @param functionId .
     */
    void deleteFunctionById(Long functionId);

    void updateFunction(PmsFunction pmsFunction);

    List<PmsFunction> listFunctionByOperatorId(Long operatorId);

    List<PmsFunction> listAllFunction();

    List<Long> listFunctionIdsByRoleId(Long roleId);

    List<PmsFunction> listFunctionByRoleId(Long roleId);

    List<PmsFunction> listFunctionByParentId(Long id);

    PmsFunction getFunctionById(Long id);

    PmsFunction getFunctionWithParentInfo(Long id);
    //</editor-fold>


    //<editor-fold desc="角色管理">

    void createRole(PmsRole pmsRole) throws BizException;

    void deleteRoleById(Long id) throws BizException;

    void updateRole(PmsRole pmsRole) throws BizException;

    List<PmsRole> listAllRoles();

    PageResult<List<PmsRole>> listRolePage(Map<String, Object> paramMap, PageParam pageParam);

    List<PmsRole> listRolesByOperatorId(Long operatorId);

    PmsRole getRoleById(Long id);

    PmsRole getRoleByName(String roleName);

    void assignPermission(Long roleId, List<Long> functionIds) throws BizException;

    //</editor-fold>


}
