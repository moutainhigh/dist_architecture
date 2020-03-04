package com.xpay.common.util.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xpay.common.statics.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Objects;

/**
 * Author: Cmf
 * Date: 2019/10/30
 * Time: 11:41
 * Description:
 */
public class JwtTokenUtil {
    private final static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    private final static String USER_TOKEN_INFO_CLAIM_KEY = "USER_TOKEN_INFO_CLAIM_KEY";


    public static <T extends Serializable> String genToken(RSAPrivateKey privateKey, T tokenInfo, Map<String, String> claims) {
        Algorithm algorithm = Algorithm.RSA256(null, privateKey);
        JWTCreator.Builder builder = JWT.create();
        claims.forEach(builder::withClaim);
        builder.withClaim(USER_TOKEN_INFO_CLAIM_KEY, JsonUtil.toString(tokenInfo));
        return builder.sign(algorithm);
    }

    /**
     * 校验token,并对claims中指定的key判断是否匹配验证，如果验证成功，则返回用户token信息：clazz类型
     *
     * @param publicKey .
     * @param token     .
     * @param claims    需要进行匹配验证的claim
     * @param clazz     用户信息class
     * @param <T>       .
     * @return 用户token信息
     * @throws BizException 某验证有误，则抛出异常
     */
    public static <T extends Serializable> T verifyToken(RSAPublicKey publicKey, String token, Map<String, String> claims, Class<T> clazz) throws BizException {
        DecodedJWT jwt;
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            jwt = verifier.verify(token);
        } catch (Exception ex) {
            logger.info("token无效");
            throw new BizException(BizException.BIZ_INVALIDATE, "token无效");
        }
        for (Map.Entry<String, String> entry : claims.entrySet()) {
            if (!Objects.equals(entry.getValue(), jwt.getClaim(entry.getKey()) == null ? null : jwt.getClaim(entry.getKey()).asString())) {
                throw new BizException(BizException.BIZ_INVALIDATE, String.format("TOKEN : %s,信息不匹配", entry.getKey()));
            }
        }
        return JsonUtil.toBean(jwt.getClaim(USER_TOKEN_INFO_CLAIM_KEY).asString(), clazz);
    }
}
