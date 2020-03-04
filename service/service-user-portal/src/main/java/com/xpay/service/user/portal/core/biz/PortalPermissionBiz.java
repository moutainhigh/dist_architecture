package com.xpay.service.user.portal.core.biz;

import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.user.portal.entity.PortalFunction;
import com.xpay.facade.user.portal.entity.PortalRole;
import com.xpay.facade.user.portal.entity.PortalRoleFunction;
import com.xpay.facade.user.portal.entity.PortalRoleOperator;
import com.xpay.service.user.portal.core.dao.PortalFunctionDao;
import com.xpay.service.user.portal.core.dao.PortalRoleDao;
import com.xpay.service.user.portal.core.dao.PortalRoleFunctionDao;
import com.xpay.service.user.portal.core.dao.PortalRoleOperatorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:43
 * Description:
 */
@Service
public class PortalPermissionBiz {
    @Autowired
    private PortalRoleOperatorDao portalRoleOperatorDao;

    @Autowired
    private PortalFunctionDao portalFunctionDao;

    @Autowired
    private PortalRoleDao portalRoleDao;

    @Autowired
    private PortalRoleFunctionDao portalRoleFunctionDao;


    //<editor-fold desc="功能点管理">

    public void createFunction(PortalFunction portalFunction) {
        if (portalFunction.getParentId() == null) {
            portalFunction.setParentId(0L);
        } else {
            PortalFunction parentFunc = portalFunctionDao.getById(portalFunction.getParentId());
            if (parentFunc == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("功能父节点不存在");
            } else if (parentFunc.getFunctionType() != PortalFunction.MENU_TYPE) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("父节点必须是菜单类型");
            }
        }
        portalFunctionDao.insert(portalFunction);
    }

    /**
     * 删除Function以及其子Function,同时也会删除与这些function相关的role_function映射关联
     * 若存在菜单类的子Function，该方法会抛出异常
     *
     * @param functionId .
     */
    @Transactional
    public void deleteFunctionById(Long functionId) {
        List<PortalFunction> subFunctions = portalFunctionDao.listByParentId(functionId);
        if (subFunctions.stream().anyMatch(p -> p.getFunctionType() == PortalFunction.MENU_TYPE)) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("存在子菜单，该菜单不能直接删除");
        }
        List<Long> deleteIds = subFunctions.stream().map(PortalFunction::getId).collect(Collectors.toList());
        deleteIds.add(functionId);
        portalFunctionDao.deleteByIdList(deleteIds);           //删除Function以及其子Function
        portalRoleFunctionDao.deleteByFunctionIds(deleteIds);  //删除关联的所有role_function
    }


    public void updateFunction(PortalFunction function) {
        portalFunctionDao.update(function);
    }

    /**
     * 根据operatorId获取得到其拥有所有的功能权限
     *
     * @param operatorId .
     * @return
     */
    public List<PortalFunction> listFunctionByOperatorId(Long operatorId) {
        if (operatorId == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("operatorId不能为空");
        }
        //获取得到所有的roleId
        List<Long> roleIds = portalRoleOperatorDao.listByOperatorId(operatorId).stream().map(PortalRoleOperator::getRoleId).collect(Collectors.toList());
        if (roleIds.size() == 0) {
            return Collections.emptyList();
        }
        return portalFunctionDao.listByRoleIds(roleIds);
    }

    /**
     * 获取得到所有的功能
     *
     * @return .
     */
    public List<PortalFunction> listAllFunction() {
        return portalFunctionDao.listAll();
    }

    public List<Long> listFunctionIdsByRoleId(Long roleId) {
        return portalFunctionDao.listByRoleIds(Collections.singletonList(roleId))
                .stream()
                .map(PortalFunction::getId)
                .collect(Collectors.toList());
    }

    public List<PortalFunction> listFunctionByRoleId(Long roleId) {
        return portalFunctionDao.listByRoleIds(Collections.singletonList(roleId));
    }


    public List<PortalFunction> listFunctionByParentId(Long parentId) {
        return portalFunctionDao.listByParentId(parentId);
    }

    public PortalFunction getFunctionById(Long id) {
        return portalFunctionDao.getById(id);
    }


    public PortalFunction getFunctionWithParentInfo(Long id) {
        PortalFunction function = portalFunctionDao.getById(id);
        if (function == null) {
            return null;
        } else {
            PortalFunction pFunction = portalFunctionDao.getById(function.getParentId());
            function.setParent(pFunction);
        }
        return function;
    }


    //</editor-fold>


    //<editor-fold desc="角色管理">

    public void createRole(PortalRole portalRole) {
        portalRoleDao.insert(portalRole);
    }


    public void deleteRoleById(Long id) {
        portalRoleDao.deleteById(id);
    }

    public void updateRole(PortalRole portalRole) {
        portalRoleDao.update(portalRole);
    }

    public List<PortalRole> listAllRoles() {
        return portalRoleDao.listAll();
    }

    public List<PortalRole> listAllAdminRoles() {
        return portalRoleDao.listAllAdminRoles();
    }

    public List<PortalRole> listRoleByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }
        return portalRoleDao.listRoleByMerchantNo(merchantNo);
    }


    public PageResult<List<PortalRole>> listRolePage(Map<String, Object> paramMap, PageParam pageParam) {
        return portalRoleDao.listPage(paramMap, pageParam);
    }

    public List<PortalRole> listRolesByOperatorId(Long operatorId) {
        List<Long> roleIds = portalRoleOperatorDao.listByOperatorId(operatorId).stream().map(PortalRoleOperator::getRoleId).collect(Collectors.toList());
        if (roleIds.size() == 0) {
            return Collections.emptyList();
        }
        return portalRoleDao.listByIdList(roleIds);

    }

    public PortalRole getRoleById(Long id) {
        return portalRoleDao.getById(id);
    }

    public PortalRole getRoleByName(String roleName) {
        return portalRoleDao.getByRoleName(roleName);
    }


    public void assignPermission(Long roleId, List<Long> functionIds) {
        if (roleId == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("roleId不能为空");
        }

        // 先删除所有的菜单权限和功能权限
        portalRoleFunctionDao.deleteByRoleId(roleId);

        //添加菜单权限
        if (functionIds != null && !functionIds.isEmpty()) {
            List<PortalRoleFunction> roleFunctionList = functionIds.stream().map(p -> {
                PortalRoleFunction portalRoleFunction = new PortalRoleFunction();
                portalRoleFunction.setFunctionId(p);
                portalRoleFunction.setRoleId(roleId);
                return portalRoleFunction;
            }).collect(Collectors.toList());
            portalRoleFunctionDao.insert(roleFunctionList);
        }
    }


    //</editor-fold>
}
