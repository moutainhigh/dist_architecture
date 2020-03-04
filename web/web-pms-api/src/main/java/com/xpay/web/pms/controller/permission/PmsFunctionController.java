/**
 *
 */
package com.xpay.web.pms.controller.permission;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.user.pms.PmsFunctionTypeEnum;
import com.xpay.common.statics.result.RestResult;
import com.xpay.facade.user.pms.entity.PmsFunction;
import com.xpay.facade.user.pms.service.PmsPermissionFacade;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PmsFunctionNodeVO;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/12
 * Time: 10:13
 * Description: 菜单权限控制器
 */
@RestController
@RequestMapping("pmsFunction")
public class PmsFunctionController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(PmsFunctionController.class);
    @Reference
    private PmsPermissionFacade pmsPermissionFacade;

    /**
     * 列出要管理的功能.
     *
     * @return PmsMenuList .
     */
    @Permission("pms:function:view")
    @RequestMapping("listAllPmsFunction")
    public RestResult<List<PmsFunction>> listAllPmsFunction() {
        List<PmsFunction> allFunctions = pmsPermissionFacade.listAllFunction();
        return RestResult.success(allFunctions);
    }

    /**
     * 保存新增功能.
     *
     * @return operateSuccess or operateError .
     */
    @Permission("pms:function:add")
    @RequestMapping("addPmsFunction")
    public RestResult<String> addPmsFunction(@RequestBody PmsFunction pmsFunction) {
        try {
            if (null != pmsFunction.getParentId()) {
                PmsFunction parentFunction = pmsPermissionFacade.getFunctionById(pmsFunction.getParentId());
                if (parentFunction.getFunctionType() != PmsFunction.MENU_TYPE) {
                    return RestResult.error("父功能必须是菜单");
                }
            }
            pmsPermissionFacade.createFunction(pmsFunction);
            super.logSave("添加功能[" + pmsFunction.getName() + "]", true);
        } catch (Exception e) {
            // 记录系统操作日志
            logger.error("== pmsFunctionAdd exception:", e);
            super.logSave("增加功能", false);
            return RestResult.error("添加功能出错");
        }
        return RestResult.success("添加功能成功");
    }

    /**
     * 保存要修改的功能.
     *
     * @return .
     */
    @Permission("pms:function:edit")
    @RequestMapping("editPmsFunction")
    public RestResult<String> editPmsFunction(@RequestBody PmsFunction pmsFunction) {
        try {
            PmsFunction current = pmsPermissionFacade.getFunctionById(pmsFunction.getId());
            current.setName(pmsFunction.getName());
            current.setNumber(pmsFunction.getNumber());
            current.setUrl(pmsFunction.getUrl());
            pmsPermissionFacade.updateFunction(current);
            // 记录系统操作日志
            super.logEdit("修改功能，功能名称[" + pmsFunction.getName() + "]", true);
            return RestResult.success("修改成功");
        } catch (Exception e) {
            // 记录系统操作日志
            logger.error("== pmsFunctionEdit exception:", e);
            super.logEdit("修改功能，功能名称[" + pmsFunction.getName() + "]", false);
            return RestResult.success("修改失败");
        }
    }


    /**
     * 删除功能.
     *
     * @return .
     */
    @Permission("pms:function:delete")
    @RequestMapping("deletePmsFunction")
    public RestResult<String> deletePmsFunction(@RequestParam("id") Long id) {
        String functionName = null;
        try {
            PmsFunction function = null;
            if (id == null || id == 0 || (function = pmsPermissionFacade.getFunctionById(id)) == null) {
                return RestResult.error("无法获取要删除的数据");
            }

            functionName = function.getName();

            // 先判断此菜单下是否有子菜单
            List<PmsFunction> childMenuList = pmsPermissionFacade.listFunctionByParentId(id).stream()
                    .filter(p -> p.getFunctionType() == PmsFunction.MENU_TYPE)
                    .collect(Collectors.toList());
            if (!childMenuList.isEmpty()) {
                return RestResult.error("此功能下关联有【" + childMenuList.size() + "】个子菜单，不能支接删除!");
            }
            // 删除掉Function以及其子Function（该方法会同时删除与这些function关联的roleFunction）
            pmsPermissionFacade.deleteFunctionById(id);

            // 记录系统操作日志
            super.logDelete("删除功能，功能名称[" + function.getName() + "]", true);
            return RestResult.success("删除成功");
        } catch (Exception e) {
            // 记录系统操作日志
            logger.error("== delPmsMenu exception:", e);
            super.logDelete("删除功能，功能名称[" + functionName + "]", false);
            return RestResult.error("删除功能出错");
        }
    }


    /**
     * 返回权限功能的树形结构
     */
    @Permission("pms:function:view")
    @GetMapping("listPmsFunctionTree")
    public RestResult<List<PmsFunctionNodeVO>> listPmsFunctionTree() {
        List<PmsFunction> allFunctions = pmsPermissionFacade.listAllFunction();
        Map<Long, List<PmsFunction>> mapByPid = allFunctions.stream().collect(Collectors.groupingBy(PmsFunction::getParentId));

        List<PmsFunctionNodeVO> nodeVOList;
        if (mapByPid.get(0L) != null) {
            nodeVOList = mapByPid.get(0L).stream()
                    .map(pmsFunction -> buildPmsFunctionNodeVo(pmsFunction, mapByPid))
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
    private PmsFunctionNodeVO buildPmsFunctionNodeVo(PmsFunction root, Map<Long, List<PmsFunction>> mapByPid) {

        PmsFunctionNodeVO pmsFunctionNodeVO = copyProperties(root);

        if (PmsFunctionTypeEnum.MENU_TYPE.getValue() == pmsFunctionNodeVO.getFunctionType()
                && mapByPid.get(root.getId()) != null) {
            mapByPid.get(root.getId()).forEach(pmsFunction -> {
                PmsFunctionNodeVO children = buildPmsFunctionNodeVo(pmsFunction, mapByPid);
                if (PmsFunctionTypeEnum.ACTION_TYPE.getValue() == pmsFunction.getFunctionType()) {
                    pmsFunctionNodeVO.getActionChildren().add(children);
                } else {
                    pmsFunctionNodeVO.getChildren().add(children);
                }
            });
        }
        return pmsFunctionNodeVO;
    }

    private PmsFunctionNodeVO copyProperties(PmsFunction pmsFunction) {
        PmsFunctionNodeVO pmsFunctionNodeVO = new PmsFunctionNodeVO();
        pmsFunctionNodeVO.setId(pmsFunction.getId());
        pmsFunctionNodeVO.setParentId(pmsFunction.getParentId());
        pmsFunctionNodeVO.setName(pmsFunction.getName());
        pmsFunctionNodeVO.setNumber(pmsFunction.getNumber());
        pmsFunctionNodeVO.setPermissionFlag(pmsFunction.getPermissionFlag());
        pmsFunctionNodeVO.setFunctionType(pmsFunction.getFunctionType());
        pmsFunctionNodeVO.setUrl(pmsFunction.getUrl());
        pmsFunctionNodeVO.setTargetName(pmsFunction.getTargetName());
        pmsFunctionNodeVO.setChildren(new ArrayList<>());
        pmsFunctionNodeVO.setActionChildren(new ArrayList<>());
        return pmsFunctionNodeVO;
    }


}
