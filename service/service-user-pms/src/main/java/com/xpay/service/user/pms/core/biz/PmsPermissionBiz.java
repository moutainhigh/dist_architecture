package com.xpay.service.user.pms.core.biz;

import com.xpay.common.statics.enums.user.pms.PmsOperatorTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.*;
import com.xpay.service.user.pms.core.dao.*;
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
public class PmsPermissionBiz {
    @Autowired
    private PmsRoleOperatorDao pmsRoleOperatorDao;

    @Autowired
    private PmsFunctionDao pmsFunctionDao;

    @Autowired
    private PmsRoleDao pmsRoleDao;

    @Autowired
    private PmsRoleFunctionDao pmsRoleFunctionDao;

    @Autowired
    private PmsOperatorDao pmsOperatorDao;


    //<editor-fold desc="功能点管理">

    public void createFunction(PmsFunction pmsFunction) {
        if (pmsFunction.getParentId() == null) {
            pmsFunction.setParentId(0L);
        } else {
            PmsFunction parentFunc = pmsFunctionDao.getById(pmsFunction.getParentId());
            if (parentFunc == null) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("功能父节点不存在");
            } else if (parentFunc.getFunctionType() != PmsFunction.MENU_TYPE) {
                throw CommonExceptions.BIZ_INVALID.newWithErrMsg("父节点必须是菜单类型");
            }
        }
        pmsFunctionDao.insert(pmsFunction);
    }

    /**
     * 删除Function以及其子Function,同时也会删除与这些function相关的role_function映射关联
     * 若存在菜单类的子Function，该方法会抛出异常
     *
     * @param functionId .
     */
    @Transactional
    public void deleteFunctionById(Long functionId) {
        List<PmsFunction> subFunctions = pmsFunctionDao.listByParentId(functionId);
        if (subFunctions.stream().anyMatch(p -> p.getFunctionType() == PmsFunction.MENU_TYPE)) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("存在子菜单，该菜单不能直接删除");
        }
        List<Long> deleteIds = subFunctions.stream().map(PmsFunction::getId).collect(Collectors.toList());
        deleteIds.add(functionId);
        pmsFunctionDao.deleteByIdList(deleteIds);           //删除Function以及其子Function
        pmsRoleFunctionDao.deleteByFunctionIds(deleteIds);  //删除关联的所有role_function
    }


    public void updateFunction(PmsFunction function) {
        pmsFunctionDao.update(function);
    }

    /**
     * 根据operatorId获取得到其拥有所有的功能权限
     *
     * @param operatorId .
     * @return
     */
    public List<PmsFunction> listFunctionByOperatorId(Long operatorId) {
        if (operatorId == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("operatorId不能为空");
        }
        //获取得到所有的roleId
        PmsOperator operator = pmsOperatorDao.getById(operatorId);
        if (operator == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("操作员不存在");
        } else if (operator.getType() == PmsOperatorTypeEnum.ADMIN.getValue()) {
            return pmsFunctionDao.listAll();
        } else {
            List<Long> roleIds = pmsRoleOperatorDao.listByOperatorId(operatorId).stream().map(PmsRoleOperator::getRoleId).collect(Collectors.toList());
            if (roleIds.size() == 0) {
                return Collections.emptyList();
            }
            return pmsFunctionDao.listByRoleIds(roleIds);
        }
    }

    /**
     * 获取得到所有的功能
     *
     * @return .
     */
    public List<PmsFunction> listAllFunction() {
        return pmsFunctionDao.listAll();
    }

    public List<Long> listFunctionIdsByRoleId(Long roleId) {
        return pmsFunctionDao.listByRoleIds(Collections.singletonList(roleId))
                .stream()
                .map(PmsFunction::getId)
                .collect(Collectors.toList());
    }

    public List<PmsFunction> listFunctionByRoleId(Long roleId) {
        return pmsFunctionDao.listByRoleIds(Collections.singletonList(roleId));
    }

    public List<PmsFunction> listFunctionByParentId(Long parentId) {
        return pmsFunctionDao.listByParentId(parentId);
    }

    public PmsFunction getFunctionById(Long id) {
        return pmsFunctionDao.getById(id);
    }


    public PmsFunction getFunctionWithParentInfo(Long id) {
        PmsFunction function = pmsFunctionDao.getById(id);
        if (function == null) {
            return null;
        } else {
            PmsFunction pFunction = pmsFunctionDao.getById(function.getParentId());
            function.setParent(pFunction);
        }
        return function;
    }


    //</editor-fold>


    //<editor-fold desc="角色管理">

    /**
     * 创建角色
     *
     * @param pmsRole 角色
     */
    public void createRole(PmsRole pmsRole) {
        if (pmsRoleDao.getByRoleName(pmsRole.getRoleName()) != null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("角色名[" + pmsRole.getRoleName() + "]已存在");
        }
        pmsRoleDao.insert(pmsRole);
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleById(Long id) {
        if (id == null || pmsRoleDao.getById(id) == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("该角色不存在");
        }
        // 先删除所有关联功能和关联操作员
        pmsRoleFunctionDao.deleteByRoleId(id);
        pmsRoleOperatorDao.deleteByRoleId(id);
        pmsRoleDao.deleteById(id);
    }

    /**
     * 更新角色
     *
     * @param pmsRole 角色
     */
    public void updateRole(PmsRole pmsRole) {
        if (pmsRole == null || pmsRole.getRoleName() == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("角色名不能为空");
        }
        PmsRole targetRole = pmsRoleDao.getByRoleName(pmsRole.getRoleName());
        if (targetRole == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("该角色不存在");
        }
        // 角色只能修改备注
        targetRole.setRemark(pmsRole.getRemark());
        pmsRoleDao.update(targetRole);
    }

    /**
     * 查询所有角色
     */
    public List<PmsRole> listAllRoles() {
        return pmsRoleDao.listAll();
    }

    /**
     * 查询角色列表
     *
     * @param paramMap  查询参数
     * @param pageParam 分页参数
     */
    public PageResult<List<PmsRole>> listRolePage(Map<String, Object> paramMap, PageParam pageParam) {
        return pmsRoleDao.listPage(paramMap, pageParam);
    }

    /**
     * 查询操作员的所有角色
     *
     * @param operatorId 操作员id
     */
    public List<PmsRole> listRolesByOperatorId(Long operatorId) {
        List<Long> roleIds = pmsRoleOperatorDao.listByOperatorId(operatorId).stream().map(PmsRoleOperator::getRoleId).collect(Collectors.toList());
        if (roleIds.size() == 0) {
            return Collections.emptyList();
        }
        return pmsRoleDao.listByIdList(roleIds);

    }

    /**
     * 根据id查询
     *
     * @param id 角色id
     */
    public PmsRole getRoleById(Long id) {
        return pmsRoleDao.getById(id);
    }

    /**
     * 根据角色名查询
     *
     * @param roleName 角色名称
     */
    public PmsRole getRoleByName(String roleName) {
        return pmsRoleDao.getByRoleName(roleName);
    }

    /**
     * 分配权限功能
     *
     * @param roleId      角色id
     * @param functionIds 功能id列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermission(Long roleId, List<Long> functionIds) {
        if (roleId == null || pmsRoleDao.getById(roleId) == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("该角色不存在");
        }

        // 先删除所有的菜单权限和功能权限
        pmsRoleFunctionDao.deleteByRoleId(roleId);

        //添加菜单权限
        if (functionIds != null && !functionIds.isEmpty()) {
            List<PmsRoleFunction> roleFunctionList = functionIds.stream().map(p -> {
                PmsRoleFunction pmsRoleFunction = new PmsRoleFunction();
                pmsRoleFunction.setFunctionId(p);
                pmsRoleFunction.setRoleId(roleId);
                return pmsRoleFunction;
            }).collect(Collectors.toList());
            pmsRoleFunctionDao.insert(roleFunctionList);
        }
    }


    //</editor-fold>
}
