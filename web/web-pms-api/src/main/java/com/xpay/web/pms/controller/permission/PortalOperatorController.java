package com.xpay.web.pms.controller.permission;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.user.portal.PortalOperatorStatusEnum;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.facade.user.portal.entity.PortalOperator;
import com.xpay.facade.user.portal.service.PortalOperatorFacade;
import com.xpay.facade.user.portal.service.PortalPermissionFacade;
import com.xpay.web.pms.annotation.CurrentUser;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PortalOperatorVO;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/21
 * Time: 10:06
 * Description:
 */
@RestController
@RequestMapping("portalOperator")
public class PortalOperatorController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference
    private PortalOperatorFacade portalOperatorFacade;

    @Reference
    private PortalPermissionFacade portalPermissionFacade;

    /**
     * 分页列出操作员信息，并可按登录名、姓名、状态进行查询.
     */
    @Permission("portal:operator:view")
    @RequestMapping("portalOperatorList")
    public RestResult<PageResult<List<PortalOperatorVO>>> portalOperatorList(@RequestParam int pageCurrent,
                                                                             @RequestParam int pageSize,
                                                                             @RequestBody PortalOperatorVO portalOperatorVO) {
        try {
            Map<String, Object> paramMap = BeanUtil.toMap(portalOperatorVO);
            PageResult<List<PortalOperator>> pageResult = portalOperatorFacade.listOperatorPage(paramMap, PageParam.newInstance(pageCurrent, pageSize));
            List<PortalOperatorVO> vos = pageResult.getData().stream().map(PortalOperatorVO::new).collect(Collectors.toList());
            return RestResult.success(PageResult.newInstance(vos, pageCurrent, pageSize, pageResult.getTotalRecord()));
        } catch (Exception e) {
            logger.error("== portalOperatorList exception:", e);
            return RestResult.error("获取操作人失败");
        }
    }

    @Permission("portal:operator:view")
    @RequestMapping("portalOperatorInfo")
    public RestResult<PortalOperatorVO> portalOperatorInfo(@RequestParam long id) {
        PortalOperator operator = portalOperatorFacade.getOperatorById(id);
        return RestResult.success(new PortalOperatorVO(operator));
    }


    @Permission("portal:operator:edit")
    @RequestMapping("portalOperatorEdit")
    public RestResult<String> portalOperatorEdit(@CurrentUser PmsOperator currentOperator, @RequestBody PortalOperatorVO portalOperatorVO) {
        try {
            PortalOperator portalOperator = portalOperatorFacade.getOperatorById(portalOperatorVO.getId());
            if (portalOperator == null) {
                return RestResult.error("无法获取要修改的操作员信息");
            }
            portalOperator.setUpdator(currentOperator.getLoginName());// 修改者
            List<Long> assignedRoles = Arrays.asList(portalOperatorVO.getRoles());
            portalOperatorFacade.updateOperatorAndAssignRoles(portalOperator, assignedRoles);
            super.logEdit("修改商户操作员[" + portalOperator.getLoginName() + "更改后角色[" + assignedRoles + "]", true);
            return RestResult.success("修改成功");
        } catch (Exception e) {
            logger.error("== portalOperatorEdit exception:", e);
            return RestResult.error("更新操作员信息失败");
        }
    }

    @RequestMapping("changeStatus")
    @Permission("portal:operator:edit")
    public RestResult<String> changeStatus(@CurrentUser PmsOperator currentOperator, @RequestParam Long id) {
        try {
            PortalOperator operator = portalOperatorFacade.getOperatorById(id);
            if (operator == null) {
                return RestResult.error("无法获取要操作的数据");
            }
            operator.setStatus(operator.getStatus() == PortalOperatorStatusEnum.ACTIVE.getValue() ? PortalOperatorStatusEnum.INACTIVE.getValue() : PortalOperatorStatusEnum.ACTIVE.getValue());
            portalOperatorFacade.updateOperator(operator);
            super.logEdit("冻结/审核操作员[" + operator.getLoginName() + "],操作后状态:" + operator.getStatus(), true);
            return RestResult.success("操作成功");
        } catch (Exception e) {
            logger.error("== changeOperatorStatus exception:", e);
            return RestResult.error("操作失败");
        }
    }
}
