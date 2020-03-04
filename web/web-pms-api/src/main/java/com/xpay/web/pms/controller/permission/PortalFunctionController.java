/**
 *
 */
package com.xpay.web.pms.controller.permission;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.result.RestResult;
import com.xpay.facade.user.portal.entity.PortalFunction;
import com.xpay.facade.user.portal.service.PortalPermissionFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PortalFunctionNodeVO;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/12
 * Time: 10:13
 * Description: 商户后台菜单权限控制器
 */
@RestController
@RequestMapping("portalFunction")
public class PortalFunctionController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(PortalFunctionController.class);
    @Reference
    private PortalPermissionFacade portalPermissionFacade;

    /**
     * 列出要管理的菜单.
     *
     * @return PortalMenuList .
     */
    @Permission("portal:function:view")
    @RequestMapping("portalFunctionList")
    public RestResult<List<PortalFunction>> portalFunctionList() {
        List<PortalFunction> allFunctions = portalPermissionFacade.listAllFunction();
        return RestResult.success(allFunctions);
    }

    /**
     * 返回权限功能的树形结构
     */
    @Permission("portal:function:view")
    @RequestMapping("portalFunctionTree")
    public RestResult<List<PortalFunctionNodeVO>> portalFunctionTree() {
        List<PortalFunction> allFunctions = portalPermissionFacade.listAllFunction();
        Map<Long, List<PortalFunction>> mapByPid = allFunctions.stream().collect(Collectors.groupingBy(PortalFunction::getParentId));

        List<PortalFunctionNodeVO> nodeVOList;
        if (mapByPid.get(0L) != null) {
            nodeVOList = mapByPid.get(0L).stream()
                    .map(portalFunction -> buildPortalFunctionNodeVo(portalFunction, mapByPid))
                    .collect(Collectors.toList());
        } else {
            nodeVOList = new ArrayList<>();
        }
        return RestResult.success(nodeVOList);
    }

    /**
     * 从根节点开始构建权限功能树
     * @param root      根节点
     * @param mapByPid  根据父节点聚集map
     */
    private PortalFunctionNodeVO buildPortalFunctionNodeVo(PortalFunction root, Map<Long, List<PortalFunction>> mapByPid) {

        PortalFunctionNodeVO portalFunctionNodeVO = copyProperties(root);

        if (PortalFunction.MENU_TYPE == portalFunctionNodeVO.getFunctionType() && mapByPid.get(root.getId()) != null) {
            mapByPid.get(root.getId()).forEach(portalFunction -> {
                PortalFunctionNodeVO children = buildPortalFunctionNodeVo(portalFunction, mapByPid);
                if (PortalFunction.ACTION_TYPE == portalFunction.getFunctionType()) {
                    portalFunctionNodeVO.getActionChildren().add(children);
                } else {
                    portalFunctionNodeVO.getChildren().add(children);
                }
            });
        }
        return portalFunctionNodeVO;
    }

    private PortalFunctionNodeVO copyProperties(PortalFunction portalFunction) {
        PortalFunctionNodeVO portalFunctionNodeVO = new PortalFunctionNodeVO();
        portalFunctionNodeVO.setId(portalFunction.getId());
        portalFunctionNodeVO.setParentId(portalFunction.getParentId());
        portalFunctionNodeVO.setName(portalFunction.getName());
        portalFunctionNodeVO.setNumber(portalFunction.getNumber());
        portalFunctionNodeVO.setPermissionFlag(portalFunction.getPermissionFlag());
        portalFunctionNodeVO.setFunctionType(portalFunction.getFunctionType());
        portalFunctionNodeVO.setUrl(portalFunction.getUrl());
        portalFunctionNodeVO.setTargetName(portalFunction.getTargetName());
        portalFunctionNodeVO.setChildren(new ArrayList<>());
        portalFunctionNodeVO.setActionChildren(new ArrayList<>());
        return portalFunctionNodeVO;
    }

    /**
     * 保存新增菜单.
     *
     * @return
     */
    @Permission("portal:function:add")
    @RequestMapping("portalFunctionAdd")
    public RestResult<String> portalFunctionAdd(@RequestBody PortalFunction portalFunction) {
        try {
            if (null != portalFunction.getParentId()) {
                PortalFunction parentFunction = portalPermissionFacade.getFunctionById(portalFunction.getParentId());
                if (parentFunction.getFunctionType() != PortalFunction.MENU_TYPE) {
                    return RestResult.error("父功能必须是菜单");
                }
            }
            portalPermissionFacade.createFunction(portalFunction);
            super.logSave("添加商户后台功能[" + portalFunction.getName() + "]", true);
            return RestResult.success("添加成功");
        } catch (Exception e) {
            // 记录系统操作日志
            logger.error("== portalFunctionAdd exception:", e);
            super.logSave("添加商户后台功能失败", false);
            return RestResult.error("添加失败");
        }
    }

    /**
     * 保存要修改的菜单.
     *
     * @return
     */
    @Permission("portal:function:edit")
    @RequestMapping("portalFunctionEdit")
    public RestResult<String> portalFunctionEdit(@RequestBody PortalFunction portalFunction) {
        try {
            PortalFunction current = portalPermissionFacade.getFunctionById(portalFunction.getId());
            current.setName(portalFunction.getName());
            current.setNumber(portalFunction.getNumber());
            current.setUrl(portalFunction.getUrl());
            portalPermissionFacade.updateFunction(current);
            // 记录系统操作日志
            super.logEdit("修改商户后台功能，功能名称[" + portalFunction.getName() + "]", true);
            return RestResult.success("修改成功");
        } catch (Exception e) {
            // 记录系统操作日志
            logger.error("== portalFunctionEdit exception:", e);
            super.logEdit("修改商户后台功能，功能名称[" + portalFunction.getName() + "]", false);
            return RestResult.success("修改失败");
        }
    }

    /**
     * 删除菜单.
     *
     * @return
     */
    @Permission("portal:function:delete")
    @RequestMapping("portalFunctionDelete")
    public RestResult<String> portalFunctionDelete(@RequestParam("id") Long id) {
        String functionName = null;
        try {
            PortalFunction function;
            if (id == null || id == 0 || (function = portalPermissionFacade.getFunctionById(id)) == null) {
                return RestResult.error("无法获取要删除的数据");
            }
            functionName = function.getName();

            // 先判断此菜单下是否有子菜单
            List<PortalFunction> childMenuList = portalPermissionFacade.listFunctionByParentId(id).stream()
                    .filter(p -> p.getFunctionType() == PortalFunction.MENU_TYPE)
                    .collect(Collectors.toList());
            if (!childMenuList.isEmpty()) {
                return RestResult.error("此功能下关联有【" + childMenuList.size() + "】个子菜单，不能支接删除!");
            }
            // 删除掉Function以及其子Function（该方法会同时删除与这些function关联的roleFunction）
            portalPermissionFacade.deleteFunctionById(id);

            // 记录系统操作日志
            super.logDelete("删除功能，功能名称[" + function.getName() + "]", true);
            return RestResult.success("删除成功");
        } catch (Exception e) {
            // 记录系统操作日志
            logger.error("== delPortalMenu exception:", e);
            super.logDelete("删除功能，功能名称[" + functionName + "]", false);
            return RestResult.error("删除功能出错");
        }
    }
}
