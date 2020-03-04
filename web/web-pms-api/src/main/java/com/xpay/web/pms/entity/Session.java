package com.xpay.web.pms.entity;

import com.alibaba.fastjson.TypeReference;
import com.xpay.common.statics.enums.common.RedisKeyPrefixEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.starter.comp.component.RedisClient;

import java.util.Set;

/**
 * Author: Cmf
 * Date: 2019/10/30
 * Time: 20:20
 * Description:
 */
public class Session {
    private RedisClient redisClient;
    private PmsUserTokenInfo tokenInfo;

    private Session() {

    }

    public static Session newSession(RedisClient redisClient, PmsUserTokenInfo tokenInfo) {
        Session session = new Session();
        session.tokenInfo = tokenInfo;
        session.redisClient = redisClient;
        return session;
    }

    public static Session getSession(RedisClient redisClient, PmsOperator pmsOperator) {
        String sessionUUID = redisClient.hget(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + pmsOperator.getLoginName(), "SESSION_UUID_KEY");
        if (sessionUUID == null) {
            return null;
        } else {
            Session currentSession = new Session();
            PmsUserTokenInfo tokenInfo = new PmsUserTokenInfo();
            tokenInfo.setSessionUUID(sessionUUID);
            tokenInfo.setPmsOperator(pmsOperator);
            currentSession.tokenInfo = tokenInfo;
            currentSession.redisClient = redisClient;
            return currentSession;
        }
    }


    public void register() {
        redisClient.hset(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + tokenInfo.getPmsOperator().getLoginName(), "SESSION_UUID_KEY", tokenInfo.getSessionUUID());
    }

    public void unregister() {
        redisClient.del(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + tokenInfo.getPmsOperator().getLoginName());
    }

    public void setAttribute(String key, Object value) {
        redisClient.hset(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + tokenInfo.getPmsOperator().getLoginName(), key, JsonUtil.toString(value));
    }

    public <T> T getAttribute(String key, Class<T> clazz) {
        String value = redisClient.hget(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + tokenInfo.getPmsOperator().getLoginName(), key);
        return JsonUtil.toBean(value, clazz);
    }

    public <T> T getAttribute(String key, TypeReference<T> typeReference) {
        String value = redisClient.hget(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + tokenInfo.getPmsOperator().getLoginName(), key);
        return JsonUtil.toBean(value, typeReference);
    }

    public Set<String> getAttributeNames() {
        return redisClient.hkeys(RedisKeyPrefixEnum.WEB_PMS_SESSION_KEY.name() + "." + tokenInfo.getPmsOperator().getLoginName());
    }

    public PmsUserTokenInfo getTokenInfo() {
        return tokenInfo;
    }
}
