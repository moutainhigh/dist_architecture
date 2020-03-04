package com.xpay.web.pms.web;

import com.xpay.common.util.utils.JwtTokenUtil;
import com.xpay.common.util.utils.RSAUtil;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.starter.comp.component.RedisClient;
import com.xpay.web.pms.contant.PermissionConstant;
import com.xpay.web.pms.entity.PmsUserTokenInfo;
import com.xpay.web.pms.entity.Session;
import com.xpay.web.pms.utils.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Author: Cmf
 * Date: 2019/10/30
 * Time: 18:49
 * Description:
 */
@Component
@RefreshScope
public class SessionManager {
    private Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private final String SESSION_KEY_IN_REQUEST_SCOPE = "SESSION_KEY_IN_REQUEST_SCOPE";

    @Value(value = "${jwtPublicKey}")
    private String jwtPublicKey;
    @Value(value = "${jwtPrivateKey}")
    private String jwtPrivateKey;

    private RSAPublicKey publicKey = null;
    private RSAPrivateKey privateKey = null;

    @Autowired
    private RedisClient redisClient;

    @PostConstruct
    public void init() {
        try {
            publicKey = RSAUtil.parsePublicKey(jwtPublicKey);
            privateKey = RSAUtil.parsePrivateKey(jwtPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册session 返回 jwt token
     *
     * @param request  .
     * @param operator .当前登录用户
     * @return
     */
    public String register(HttpServletRequest request, PmsOperator operator) {
        Map<String, String> claims = new HashMap<>();
        claims.put("UserAgent", request.getHeader("User-Agent"));
        claims.put("Host", WebUtil.getIpAddr(request));
        PmsUserTokenInfo tokenInfo = new PmsUserTokenInfo();
        tokenInfo.setPmsOperator(operator);
        tokenInfo.setSessionUUID(UUID.randomUUID().toString());
        String token = JwtTokenUtil.genToken(privateKey, tokenInfo, claims);
        Session newSession = Session.newSession(redisClient, tokenInfo);
        newSession.register();
        request.setAttribute(SESSION_KEY_IN_REQUEST_SCOPE, newSession);
        return token;
    }

    public void unRegister(Session session) {
        session.unregister();
    }

    /**
     * 获取当前用户的session
     *
     * @param request .
     * @return .
     */
    public Session getSession(HttpServletRequest request) {
        if (request.getAttribute(SESSION_KEY_IN_REQUEST_SCOPE) != null) {
            return ((Session) request.getAttribute(SESSION_KEY_IN_REQUEST_SCOPE));
        }
        String token = request.getHeader(PermissionConstant.REQUEST_TOKEN_HEADER);
        Map<String, String> claims = new HashMap<>();
        claims.put("UserAgent", request.getHeader("User-Agent"));
        claims.put("Host", WebUtil.getIpAddr(request));
        PmsUserTokenInfo tokenInfo;
        try {
            tokenInfo = JwtTokenUtil.verifyToken(publicKey, token, claims, PmsUserTokenInfo.class);
        } catch (Exception ex) {
            logger.info("校验token失败", ex);
            return null;
        }
        Session session = Session.getSession(redisClient, tokenInfo.getPmsOperator());
        if (session == null || !Objects.equals(session.getTokenInfo().getSessionUUID(), tokenInfo.getSessionUUID())) {
            return null;
        }
        request.setAttribute(SESSION_KEY_IN_REQUEST_SCOPE, session);
        return session;
    }
}
