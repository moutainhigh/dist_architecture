package com.xpay.web.pms.controller.permission;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.enums.user.pms.PmsOperatorStatusEnum;
import com.xpay.common.statics.enums.user.pms.PmsOperatorTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.ValidateUtil;
import com.xpay.facade.user.pms.entity.PmsOperateLog;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.facade.user.pms.entity.PmsRole;
import com.xpay.facade.user.pms.service.PmsOperateLogFacade;
import com.xpay.facade.user.pms.service.PmsOperatorFacade;
import com.xpay.facade.user.pms.service.PmsPermissionFacade;
import com.xpay.web.pms.annotation.CurrentUser;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.permission.PmsOperateLogQueryVO;
import com.xpay.web.pms.vo.permission.PmsOperatorQueryVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/21
 * Time: 10:06
 * Description:
 */
@RestController
@RequestMapping("pmsOperator")
public class PmsOperatorController extends BaseController {
    @Reference
    private PmsOperatorFacade pmsOperatorFacade;
    @Reference
    private PmsPermissionFacade pmsPermissionFacade;
    @Reference
    private PmsOperateLogFacade pmsOperateLogFacade;

    /**
     * 分页列出操作员信息，并可按登录名、姓名、状态进行查询.
     */
    @Permission("pms:operator:view")
    @RequestMapping("listPmsOperator")
    public RestResult<PageResult<List<PmsOperator>>> listPmsOperator(@RequestParam int pageCurrent,
                                                                     @RequestParam int pageSize,
                                                                     @RequestBody PmsOperatorQueryVO pmsOperatorQueryVO) {

        PageResult<List<PmsOperator>> pageResult = pmsOperatorFacade.listOperatorPage(BeanUtil.toMap(pmsOperatorQueryVO), PageParam.newInstance(pageCurrent, pageSize));
        pageResult.getData().forEach(p -> p.setLoginPwd("******"));
        return RestResult.success(pageResult);
    }


    /**
     * 新增操作员信息
     */
    @Permission("pms:operator:add")
    @RequestMapping("addPmsOperator")
    public RestResult<String> addPmsOperator(@CurrentUser PmsOperator currentOperator, @RequestBody PmsOperator pmsOperator) {
        if (!ValidateUtil.validPassword(pmsOperator.getLoginPwd(), 8, 20, true, true, true)) {
            return RestResult.error("登录密码必须由字母、数字、特殊符号组成,8--20位");
        }
        PmsOperator newOperator = new PmsOperator();
        newOperator.setLoginName(pmsOperator.getLoginName());
        newOperator.setLoginPwd(DigestUtils.sha1Hex(pmsOperator.getLoginPwd()));
        newOperator.setRemark(pmsOperator.getRemark());
        newOperator.setRealName(pmsOperator.getRealName());
        newOperator.setMobileNo(pmsOperator.getMobileNo());
        newOperator.setStatus(PmsOperatorStatusEnum.UNAUDITED.getValue());
        newOperator.setType(PmsOperatorTypeEnum.USER.getValue());
        newOperator.setIsChangedPwd(0);
        newOperator.setCreator(currentOperator.getLoginName());

        try {
            validatePmsOperator(pmsOperator);
        } catch (BizException ex) {
            return RestResult.error(ex.getErrMsg());
        }

        List<Long> assignedRoles = pmsOperator.getRoleIds();
        pmsOperatorFacade.insertOperatorAndAssignRoles(newOperator, assignedRoles);
        super.logEdit("新增操作员[" + newOperator.getLoginName() + "新增后角色[" + assignedRoles + "]", true);
        return RestResult.success("新增成功");
    }

    /**
     * 查询单个操作员信息，同时查询其分配的角色id
     *
     * @param operatorId .
     * @return .
     */
    @RequestMapping("getPmsOperatorById")
    public RestResult<PmsOperator> getPmsOperatorById(@RequestParam long operatorId) {
        PmsOperator pmsOperator = pmsOperatorFacade.getOperatorById(operatorId);
        pmsOperator.setLoginPwd("******");
        List<PmsRole> pmsRoles = pmsPermissionFacade.listRolesByOperatorId(operatorId);
        pmsOperator.setRoleIds(pmsRoles.stream().map(PmsRole::getId).collect(Collectors.toList()));
        return RestResult.success(pmsOperator);
    }

    /**
     * 保存修改后的操作员信息
     *
     * @return operateSuccess or operateError .
     */
    @Permission("pms:operator:edit")
    @RequestMapping("editPmsOperator")
    public RestResult<String> editPmsOperator(@CurrentUser PmsOperator currentOperator, @RequestBody PmsOperator updateInfo) {
        PmsOperator pmsOperator = pmsOperatorFacade.getOperatorById(updateInfo.getId());
        if (pmsOperator == null) {
            return RestResult.error("操作员不存在在");
        }
        // 普通操作员没有修改超级管理员的权限
        if (PmsOperatorTypeEnum.ADMIN.getValue() == pmsOperator.getType()
                && PmsOperatorTypeEnum.ADMIN.getValue() != currentOperator.getType()) {
            return RestResult.error("无权修改超级管理员信息");
        }
        //普通操作员只能修改自己的信息
        pmsOperator.setRealName(updateInfo.getRealName());
        pmsOperator.setMobileNo(updateInfo.getMobileNo());
        pmsOperator.setRemark(updateInfo.getRemark());
        pmsOperator.setUpdator(currentOperator.getLoginName());// 修改者

        try {
            validatePmsOperator(pmsOperator);
        } catch (BizException ex) {
            return RestResult.error(ex.getErrMsg());
        }

        List<Long> assignedRoles = updateInfo.getRoleIds();
        pmsOperatorFacade.updateOperatorAndAssignRoles(pmsOperator, assignedRoles);
        super.logEdit("修改操作员[" + pmsOperator.getLoginName() + "更改后角色[" + assignedRoles + "]", true);
        return RestResult.success("修改成功");
    }


    /**
     * 删除操作员
     *
     * @return
     */
    @Permission("pms:operator:delete")
    @RequestMapping("deletePmsOperator")
    public RestResult<String> deletePmsOperator(@RequestParam Long id) {
        PmsOperator pmsOperator = pmsOperatorFacade.getOperatorById(id); // 查询操作员信息
        if (pmsOperator == null) {
            return RestResult.error("操作员不存在");
        }
        if (Objects.equals(pmsOperator.getType(), PmsOperatorTypeEnum.ADMIN.getValue())) {
            return RestResult.error("超级管理员不可删除");
        }
        pmsOperatorFacade.deleteOperatorById(id);
        super.logDelete("删除操作员，名称:" + pmsOperator.getLoginName(), true);
        return RestResult.success("删除操作员成功");
    }


    /**
     * 重置操作员密码.
     *
     * @return
     */
    @Permission("pms:operator:resetpwd")
    @RequestMapping("resetPmsOperatorPwd")
    public RestResult<String> resetPmsOperatorPwd(@CurrentUser PmsOperator currentOperator, @RequestParam Long operatorId, @RequestParam String newPwd) {
        PmsOperator operator = pmsOperatorFacade.getOperatorById(operatorId);
        if (operator == null) {
            return RestResult.error("操作员不存在");
        }
        // 普通操作员没有修改超级管理员的权限
        if (PmsOperatorTypeEnum.ADMIN.getValue() == operator.getType()
                && PmsOperatorTypeEnum.ADMIN.getValue() != currentOperator.getType()) {
            return RestResult.error("你没有修改超级管理员的权限");
        }
        if (!ValidateUtil.validPassword(newPwd, 8, 20, true, true, true)) {
            return RestResult.error("登录密码必须由字母、数字、特殊符号组成,8--20位");
        }
        pmsOperatorFacade.updateOperatorPwd(operator.getId(), DigestUtils.sha1Hex(newPwd), false);
        super.logEdit("重置操作员[" + operator.getLoginName() + "]的密码", true);
        return RestResult.success("密码重置成功");
    }


    /**
     * 修改操作员状态
     *
     * @return operateSuccess or operateError .
     */
    @Permission("pms:operator:changestatus")
    @RequestMapping("changePmsOperatorStatus")
    public RestResult<String> changePmsOperatorStatus(@CurrentUser PmsOperator currentOperator, @RequestParam Long id) {
        if (Objects.equals(currentOperator.getId(), id)) {
            return RestResult.error("不能修改自己账户的状态");
        }
        PmsOperator operator = pmsOperatorFacade.getOperatorById(id);
        if (operator == null) {
            return RestResult.error("操作员不存在");
        }

        // 普通操作员没有修改超级管理员的权限
        if (PmsOperatorTypeEnum.ADMIN.getValue() == operator.getType()) {
            return RestResult.error("超级管理员状态不允许修改");
        }

        if (operator.getStatus() == PmsOperatorStatusEnum.UNAUDITED.getValue() && Objects.equals(currentOperator.getLoginName(), operator.getLoginName())) {
            //如果是未审核
            return RestResult.error("创建人与审核人不能相同");
        }
        Integer oldStatus = operator.getStatus();
        if (oldStatus == PmsOperatorStatusEnum.UNAUDITED.getValue()) {
            operator.setStatus(PmsOperatorStatusEnum.ACTIVE.getValue());
        } else if (oldStatus == PmsOperatorStatusEnum.ACTIVE.getValue()) {
            operator.setStatus(PmsOperatorStatusEnum.INACTIVE.getValue());
        } else if (oldStatus == PmsOperatorStatusEnum.INACTIVE.getValue()) {
            operator.setStatus(PmsOperatorStatusEnum.ACTIVE.getValue());
        }
        pmsOperatorFacade.updateOperator(operator);
        super.logEdit("修改操作员状态成功[" + operator.getLoginName() + "],oldStatus" + oldStatus + ",newStatus:" + operator.getStatus(), true);
        return RestResult.success("操作成功");
    }

    @Permission("pms:operateLog:view")
    @RequestMapping("listOperateLogPage")
    public RestResult<PageResult<List<PmsOperateLog>>> listOperateLogPage(@RequestBody PmsOperateLogQueryVO pmsOperatorVO,
                                                                          @RequestParam int pageCurrent,
                                                                          @RequestParam int pageSize) {
        PageResult<List<PmsOperateLog>> pageResult = pmsOperateLogFacade.listOperateLogPage(BeanUtil.toMap(pmsOperatorVO), PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }


    /**
     * 校验输入的操作员数据
     *
     * @param operator .
     * @throws BizException .
     */
    private void validatePmsOperator(PmsOperator operator) throws BizException {
        if (!ValidateUtil.isStrLengthValid(operator.getRealName(), 2, 15)) {
            throw new BizException("真实姓名长度必须为2--15");
        } else if (!operator.getRealName().matches("[^\\x00-\\xff]+")) {
            throw new BizException("真实姓名必须为中文");
        } else if (!ValidateUtil.isStrLengthValid(operator.getLoginName(), 3, 50)) {
            throw new BizException("登录名长度必须为3--50");
        } else if (!ValidateUtil.isMobile(operator.getMobileNo())) {
            throw new BizException("手机号无效");
        }
    }


}
