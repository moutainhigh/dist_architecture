package com.xpay.web.pms.controller.permission;

import com.alibaba.fastjson.JSON;
import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.facade.user.pms.entity.PmsFunction;
import com.xpay.facade.user.pms.entity.PmsRole;
import com.xpay.facade.user.pms.service.PmsOperatorFacade;
import com.xpay.facade.user.pms.service.PmsPermissionFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PmsRoleVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/18
 * Time: 11:13
 * Description:
 */
@RestController
@RequestMapping("pmsRole")
public class PmsRoleController extends BaseController {

    @Reference
    private PmsPermissionFacade pmsPermissionFacade;

    @Reference
    private PmsOperatorFacade pmsOperatorFacade;

    /**
     * 获取角色列表
     */
    @Permission("pms:role:view")
    @PostMapping("listPmsRole")
    public RestResult<PageResult<List<PmsRole>>> listPmsRole(@RequestParam(required = false) String roleName,
                                                             @RequestParam int pageCurrent,
                                                             @RequestParam int pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNotBlank(roleName)) {
            paramMap.put("roleName", roleName);
        }
        PageResult<List<PmsRole>> pageResult = pmsPermissionFacade.listRolePage(paramMap, PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }

    /**
     * 获取所有角色
     */
    @Permission("pms:operator:view")
    @GetMapping("listAllPmsRoles")
    public RestResult<List<PmsRole>> listAllPmsRoles() {
        List<PmsRole> allRoleList = pmsPermissionFacade.listAllRoles();
        return RestResult.success(allRoleList);
    }

    /**
     * 保存新添加的一个角色
     */
    @Permission("pms:role:add")
    @PostMapping("addPmsRole")
    public RestResult<String> addPmsRole(@RequestBody @Validated PmsRoleVO pmsRoleVO) {
        PmsRole pmsRole = new PmsRole();
        pmsRole.setRoleName(pmsRoleVO.getRoleName());
        pmsRole.setRemark(pmsRoleVO.getRemark());
        pmsRole.setCreateTime(new Date());
        pmsPermissionFacade.createRole(pmsRole);
        // 记录操作员操作日志
        super.logSave("添加角色，角色名称[" + pmsRole.getRoleName() + "]", true);
        return RestResult.success("添加角色成功");
    }

    /**
     * 更新角色
     */
    @Permission("pms:role:edit")
    @PostMapping("editPmsRole")
    public RestResult<String> editPmsRole(@RequestBody @Validated PmsRoleVO pmsRoleVO) {
        PmsRole pmsRole = new PmsRole();
        pmsRole.setRoleName(pmsRoleVO.getRoleName());
        pmsRole.setRemark(pmsRoleVO.getRemark());
        pmsPermissionFacade.updateRole(pmsRole);
        // 记录操作员操作日志
        super.logEdit("修改角色，角色名称[" + pmsRoleVO.getRoleName() + "]", true);
        return RestResult.success("修改角色成功");
    }

    /**
     * 删除一个角色
     */
    @Permission("pms:role:delete")
    @PostMapping("deletePmsRole")
    public RestResult<String> deletePmsRole(@RequestParam Long id) {
        PmsRole role = pmsPermissionFacade.getRoleById(id);
        if (role == null) {
            return RestResult.error("无法获取要删除的角色");
        }

        pmsPermissionFacade.deleteRoleById(id);
        super.logDelete("删除角色，名称:" + role.getRoleName(), true);
        return RestResult.success("删除角色成功");
    }

    /**
     * 列出角色关联的菜单
     *
     * @return PmsMenuList .
     */
    @Permission("pms:role:assignFunction")
    @GetMapping("listPmsRoleFunction")
    public RestResult<List<PmsFunction>> listPmsRoleFunction(@RequestParam Long roleId) {
        List<PmsFunction> functions = pmsPermissionFacade.listFunctionByRoleId(roleId);
        return RestResult.success(functions);
    }

    /**
     * 分配功能
     */
    @Permission("pms:role:assignFunction")
    @PostMapping("assignPmsRoleFunction")
    public RestResult<String> assignPmsRoleFunction(@RequestParam Long roleId,
                                                    @RequestBody List<Long> functionIds) {
        PmsRole pmsRole = pmsPermissionFacade.getRoleById(roleId);
        if (pmsRole == null) {
            return RestResult.error("无法获取角色信息");
        }

        // 分配菜单权限，功能权限
        pmsPermissionFacade.assignPermission(roleId, functionIds);
        super.logEdit("修改角色[" + pmsRole.getRoleName() + "]的功能，功能ID[" + JSON.toJSONString(functionIds) + "]", true);
        return RestResult.success("分配功能成功");
    }


}
