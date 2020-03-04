package com.xpay.web.pms.web;

import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.web.pms.annotation.CurrentUser;
import com.xpay.web.pms.entity.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 增加方法注入，将含有CurrentUser注解的方法参数注入当前登录用户
 *
 * @author Derek
 * @date 2018/04/09
 */
@Component
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private SessionManager sessionManager;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //如果参数类型是User并且有CurrentUser注解则支持
        return parameter.getParameterType().isAssignableFrom(PmsOperator.class) && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Session session = sessionManager.getSession((HttpServletRequest) (webRequest.getNativeRequest()));
        return session.getTokenInfo().getPmsOperator();
    }
}
