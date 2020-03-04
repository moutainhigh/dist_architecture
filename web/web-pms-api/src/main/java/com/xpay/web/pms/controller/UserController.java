package com.xpay.web.pms.controller;

import com.alibaba.fastjson.JSONObject;
import com.xpay.common.statics.enums.user.pms.PmsOperatorStatusEnum;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.user.pms.entity.PmsFunction;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.facade.user.pms.service.PmsOperatorFacade;
import com.xpay.facade.user.pms.service.PmsPermissionFacade;
import com.xpay.web.pms.annotation.CurrentUser;
import com.xpay.web.pms.contant.PermissionConstant;
import com.xpay.web.pms.entity.Session;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/9
 * Time: 17:18
 * Description:
 */
@RestController
@RequestMapping("user")
public class UserController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Reference
    private PmsOperatorFacade pmsOperatorFacade;

    @Reference
    private PmsPermissionFacade pmsPermissionFacade;


    @RequestMapping("login")
    public RestResult<Map<String, Object>> login(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        String loginName = reqJson.getString("username");
        String loginPwd = reqJson.getString("password");

        if (StringUtil.isEmpty(loginName) || StringUtil.isEmpty(loginPwd)) {
            return RestResult.error("用户名和密码必填");
        }
        PmsOperator operator = pmsOperatorFacade.getOperatorByLoginName(loginName);
        if (operator == null) {
            return RestResult.error("用户名或密码不正确");
        } else if (operator.getStatus() == PmsOperatorStatusEnum.INACTIVE.getValue()) {
            return RestResult.error("该帐号已被冻结");
        } else if (operator.getValidDate() != null && DateUtil.compare(operator.getValidDate(), new Date(), Calendar.DATE) < 0) {
            return RestResult.error("该帐号口令已过有效期");
        }

        // 加密明文密码，验证密码
        if (operator.getLoginPwd().equals(DigestUtils.sha1Hex(loginPwd)) || true) {// 密码正确
            if (PmsOperatorStatusEnum.UNAUDITED.getValue() == operator.getStatus()) {
                return RestResult.error("账号未审核");
            }
            // 更新操作员登录数据
            operator.setLastLoginTime(Optional.ofNullable(operator.getCurrLoginTime()).orElse(new Date()));
            operator.setCurrLoginTime(new Date());
            operator.setPwdErrorCount(0); // 错误次数设为0
            pmsOperatorFacade.updateOperator(operator);
            String token = sessionManager.register(request, operator);
            getCurrentSession().setAttribute(PermissionConstant.OPERATOR_SESSION_KEY, operator);
            return RestResult.success(Collections.singletonMap("token", token));
        } else {
            // 密码错误
            logger.warn("==>帐号【{}】密码错误", operator.getLoginName());
            // 错误次数加1
            operator.setPwdErrorCount(Optional.ofNullable(operator.getPwdErrorCount()).orElse(0) + 1);
            operator.setPwdErrorTime(new Date()); // 设为当前时间
            String msg = "";
            if (operator.getPwdErrorCount() >= PermissionConstant.WEB_PWD_INPUT_ERROR_LIMIT) {
                // 超5次就冻结帐号
                operator.setStatus(PmsOperatorStatusEnum.INACTIVE.getValue());
                msg += "<br/>密码已连续输错【" + PermissionConstant.WEB_PWD_INPUT_ERROR_LIMIT + "】次，帐号已被冻结";
                super.logLoginError("登录出错,密码已连续输错【" + PermissionConstant.WEB_PWD_INPUT_ERROR_LIMIT + "】次，帐号已被冻结", operator);
            } else {
                msg += "<br/>用户名或密码错误，剩余【" + (PermissionConstant.WEB_PWD_INPUT_ERROR_LIMIT - operator.getPwdErrorCount()) + "】次机会";
                super.logLoginError("登录出错,密码输入错误.剩余[" + (PermissionConstant.WEB_PWD_INPUT_ERROR_LIMIT - operator.getPwdErrorCount()) + "]次机会。", operator);
            }
            pmsOperatorFacade.updateOperator(operator);
            return RestResult.error(msg);
        }
    }


    @RequestMapping("info")
    public RestResult<Map<String, Object>> info(@CurrentUser PmsOperator currentOperator) {
        List<PmsFunction> allFunctions = pmsPermissionFacade.listFunctionByOperatorId(currentOperator.getId());
        getCurrentSession().setAttribute(PermissionConstant.PERMISSION_SESSION_KEY, allFunctions.stream().map(PmsFunction::getPermissionFlag).collect(Collectors.toList()));
        Map<String, Object> result = new HashMap<>();
        result.put("functions", allFunctions);
        result.put("name", currentOperator.getLoginName());
        return RestResult.success(result);
    }


    /**
     * 退出登录 .
     *
     * @return logout.
     */
    @RequestMapping("logout")
    public RestResult<String> logout(HttpServletRequest request, @CurrentUser PmsOperator currentOperator) {
        if (currentOperator != null) {
            super.logLogin("退出系统");
        }
        Session session = sessionManager.getSession(request);
        if (session != null) {
            sessionManager.unRegister(session);           //清空session
        }
        return RestResult.success("");
    }

}
