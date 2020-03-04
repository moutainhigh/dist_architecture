package com.xpay.web.pms.controller;

import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.web.pms.entity.Session;
import com.xpay.web.pms.web.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: Cmf
 * Date: 2019/10/11
 * Time: 9:51
 * Description:
 */
public abstract class BaseController {

    @Autowired
    protected SessionManager sessionManager;

    public Session getCurrentSession() {
        return sessionManager.getSession(getCurrentRequest());
    }

    public HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    /**
     * 登录系统时记录日志.
     *
     * @param content
     */
    protected void logLogin(String content) {

    }

    /**
     * 登录系统失败时记录日志.
     *
     * @param content
     */
    protected void logLoginError(String content, PmsOperator operator) {

    }

    /**
     * 登录系统失败时记录日志.
     *
     * @param content
     */
    protected void logLoginError(String loginName, String content) {

    }

    /**
     * 保存数据的时记录日志.
     *
     * @param content .
     * @param success 成功？失败
     */
    protected void logSave(String content, boolean success) {

    }

    /**
     * 更新数据时记录日志.
     *
     * @param content
     * @param success 成功？失败
     */
    protected void logEdit(String content, boolean success) {

    }

    /**
     * 删除数据时记录日志.
     *
     * @param content
     * @param success 成功？失败
     */
    protected void logDelete(String content, boolean success) {

    }

    /**
     * 查询数据时记录日志.
     *
     * @param content
     * @param success 成功？失败
     */
    protected void logQuery(String content, boolean success) {

    }
}
