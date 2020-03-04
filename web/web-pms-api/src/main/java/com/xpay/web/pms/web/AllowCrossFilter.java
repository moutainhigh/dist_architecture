package com.xpay.web.pms.web;

import com.xpay.web.pms.contant.PermissionConstant;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: Cmf
 * Date: 2019/10/24
 * Time: 16:39
 * Description:
 */
@Component
public class AllowCrossFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Method", "*");
        response.addHeader("Access-Control-Allow-Headers", "content-type," + PermissionConstant.REQUEST_TOKEN_HEADER);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
