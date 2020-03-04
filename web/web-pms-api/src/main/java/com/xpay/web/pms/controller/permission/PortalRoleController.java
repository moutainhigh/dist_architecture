package com.xpay.web.pms.controller.permission;

import com.alibaba.fastjson.JSON;
import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.user.portal.PortalRoleTypeEnum;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.facade.user.portal.entity.PortalFunction;
import com.xpay.facade.user.portal.entity.PortalRole;
import com.xpay.facade.user.portal.service.PortalOperatorFacade;
import com.xpay.facade.user.portal.service.PortalPermissionFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PortalRoleVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("portalRole")
public class PortalRoleController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private PortalPermissionFacade portalPermissionFacade;

    @Reference
    private PortalOperatorFacade portalOperatorFacade;

    /**
     * 获取角色列表
     */
    @Permission("portal:role:view")
    @PostMapping("portalRoleList")
    public RestResult<PageResult<List<PortalRole>>> portalRoleList(@RequestParam(required = false) String roleName,
                                                                   @RequestBody PageParam pageParam) {
        try {
            Map<String, Object> paramMap = new HashMap<>();
            if (StringUtils.isNotBlank(roleName)) {
                paramMap.put("roleName", roleName);
            }

            PageResult<List<PortalRole>> pageResult = portalPermissionFacade.listRolePage(paramMap, pageParam);
            return RestResult.success(pageResult);
        } catch (Exception e) {
            logger.error("== listPortalRole exception:", e);
            return RestResult.error("获取角色失败");
        }
    }

    /**
     * 保存新添加的一个角色
     */
    @Permission("portal:role:add")
    @PostMapping("portalRoleAdd")
    public RestResult<String> portalRoleAdd(@RequestBody @Validated PortalRoleVO portalRoleDTO) {
        try {
            PortalRole portalRole = new PortalRole();
            portalRole.setMerchantNo("");
            portalRole.setRoleName(portalRoleDTO.getRoleName());
            portalRole.setRemark(portalRoleDTO.getRemark());
            if (portalPermissionFacade.getRoleByName(portalRole.getRoleName()) != null) {
                return RestResult.error("角色名【" + portalRole.getRoleName() + "】已存在");
            }
            portalRole.setCreateTime(new Date());
            portalRole.setRoleType(PortalRoleTypeEnum.PORTAL_ADMIN.getValue());
            portalPermissionFacade.createRole(portalRole);
            // 记录操作员操作日志
            super.logSave("添加角色，角色名称[" + portalRole.getRoleName() + "]", true);
            return RestResult.success("添加角色成功");
        } catch (Exception e) {
            logger.error("== addPortalRole exception:", e);
            return RestResult.error("添加角色失败");
        }
    }

    /**
     * 删除一个角色
     */
    @Permission("portal:role:delete")
    @PostMapping("portalRoleDelete")
    public RestResult<String> portalRoleDelete(@RequestParam Long id) {
        try {
            PortalRole role = portalPermissionFacade.getRoleById(id);
            if (role == null) {
                return RestResult.error("无法获取要删除的角色");
            }

            portalPermissionFacade.deleteRoleById(id);
            super.logDelete("删除角色，名称:" + role.getRoleName(), true);
            return RestResult.success("删除角色成功");
        } catch (Exception e) {
            logger.error("== deletePortalRole exception:", e);
            super.logDelete("删除角色出错:" + e.getMessage(), false);
            return RestResult.error("删除失败");
        }
    }


    /**
     * 列出角色关联的菜单
     *
     * @return PortalMenuList .
     */
    @Permission("portal:role:assignpermission")
    @GetMapping("portalRoleFunctionList")
    public RestResult<List<PortalFunction>> portalFunctionList(@RequestParam Long roleId) {
        List<PortalFunction> functions = portalPermissionFacade.listFunctionByRoleId(roleId);
        return RestResult.success(functions);
    }

    /**
     * 分配功能
     */
    @Permission("portal:role:assignpermission")
    @PostMapping("portalRoleAssignPermission")
    public RestResult<String> portalRoleAssignPermission(@RequestParam Long roleId,
                                                         @RequestBody List<Long> functionIds) {
        try {
            PortalRole portalRole = portalPermissionFacade.getRoleById(roleId);
            if (portalRole == null) {
                return RestResult.error("无法获取角色信息");
            }

            // 分配菜单权限，功能权限
            portalPermissionFacade.assignPermission(roleId, functionIds);
            super.logEdit("修改角色[" + portalRole.getRoleName() + "]的s功能，功能ID[" + JSON.toJSONString(functionIds) + "]", true);
            return RestResult.success("分配功能成功");
        } catch (Exception e) {
            logger.error("分配功能出现错误!", e);
            return RestResult.error("分配功能出现错误");
        }
    }

    /**
     * 获取所有角色
     */
    @Permission("portal:operator:view")
    @GetMapping("getAllPortalRoles")
    public RestResult<List<PortalRole>> getAllPortalRoles() {
        try {
            List<PortalRole> allRoleList = portalPermissionFacade.listAllRoles();
            return RestResult.success(allRoleList);
        } catch (Exception e) {
            logger.error("==getAllPortalRoles exception:", e);
            return RestResult.error("获取角色信息异常");
        }
    }

    @RequestMapping("getAllAdminRoles")
    public RestResult<List<PortalRole>> getAllAdminRoles() {
        try {
            List<PortalRole> allRoleList = portalPermissionFacade.listAllAdminRoles();
            return RestResult.success(allRoleList);
        } catch (Exception e) {
            logger.error("==getAllAdminRoles exception:", e);
            return RestResult.error("获取角色信息异常");
        }
    }

    @RequestMapping("getRolesByMerchantNo")
    public RestResult<List<PortalRole>> getRolesByMerchantNo(@RequestParam String merchantNo) {
        try {
            List<PortalRole> merchantRoles = portalPermissionFacade.listRoleByMerchantNo(merchantNo);
            return RestResult.success(merchantRoles);
        } catch (Exception e) {
            logger.error("==getRolesByMerchantNo exception:", e);
            return RestResult.error("获取角色信息异常");
        }
    }


    @RequestMapping("listRolesByOperatorId")
    public RestResult<List<PortalRole>> listRolesByOperatorId(@RequestParam long id) {

        try {
            List<PortalRole> roles = portalPermissionFacade.listRolesByOperatorId(id);
            return RestResult.success(roles);
        } catch (Exception e) {
            logger.error("==listRolesByOperatorId exception:", e);
            return RestResult.error("获取角色信息异常");
        }
    }
}
