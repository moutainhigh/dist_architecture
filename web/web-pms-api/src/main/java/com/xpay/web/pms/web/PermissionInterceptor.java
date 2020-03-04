package com.xpay.web.pms.web;

import com.alibaba.fastjson.TypeReference;
import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.web.pms.contant.PermissionConstant;
import com.xpay.web.pms.entity.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Author: Cmf
 * Date: 2019/10/9
 * Time: 17:17
 * Description:
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    private SessionManager sessionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) {
            return false;
        }
        Session session = sessionManager.getSession(request);
        if (session == null) {
            writeDenyResult(response, RestResult.unAuth("token无效"));
            return false;
        }
        //当前操作员
        PmsOperator pmsOperator = session.getAttribute(PermissionConstant.OPERATOR_SESSION_KEY, PmsOperator.class);
        if (pmsOperator == null) {
            writeDenyResult(response, RestResult.unAuth("请重新登录"));
            return false;
        }
        logger.debug("=== 当前操作员: {}", pmsOperator.getLoginName());

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        Method invocation = ((HandlerMethod) handler).getMethod();
        // 判断该方法是否加了@Permission注解
        if (!invocation.isAnnotationPresent(Permission.class)) {
            return true;
        }

        // 得到方法上的Permission注解值
        final Permission permission = invocation.getAnnotation(Permission.class);
        logger.debug("=== Invoke PermissionInterceptor. Permission: {}", permission.value());

        //用户权限列表
        List<String> permissions = session.getAttribute(PermissionConstant.PERMISSION_SESSION_KEY, new TypeReference<List<String>>() {
        });
        if (permissions != null && permissions.contains(permission.value())) { // 拥有此功能点权限
            // 执行被拦截的方法，如果此方法不调用，则被拦截的方法不会被执行
            logger.info("=== Permission Accessed: [{}], Operator: [{}]", permission.value(), pmsOperator.getLoginName());
            return true;
        } else {
            // 没有此功能权限
            logger.info("=== Permission Denied: [{}], Operator: [{}]", permission.value(), pmsOperator.getLoginName());
            writeDenyResult(response, RestResult.deny("无权限"));
            return false;
        }
    }

    private void writeDenyResult(HttpServletResponse response, RestResult restResult) throws Exception {
        response.getWriter().write(JsonUtil.toString(restResult));
        response.setContentType("application/json;charset=utf-8");
        response.flushBuffer();
    }
}
