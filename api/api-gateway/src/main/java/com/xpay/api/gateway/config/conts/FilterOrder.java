package com.xpay.api.gateway.config.conts;

/**
 * @description 过滤器执行顺序的常量配置类
 * @author: chenyf
 * @Date: 2019-02-20
 */
public class FilterOrder {
    //从-30到-21是非业务相关的前置全局过滤器，如：黑名单过滤器
    public final static int IP_BLACKLIST_FILTER = -30;//IP黑名单过滤器
    public final static int BIZ_OFF_FILTER = -29;//业务停用过滤器(主要用在某些业务线整体维护时使用)

    //从-20到-1是业务相关的全局过滤器
    public final static int REQUEST_READ_FILTER = -20; //读取请求体内容，并缓存
    public final static int REQUEST_PARAM_CHECK_FILTER = -19; //请求参数校验
    public final static int REQUEST_AUTH_FILTER = -18;  //请求参数签名验证
    public final static int REQUEST_DECRYPT_FILTER = -17; //请求参数的敏感信息密钥解密
    public final static int REWRITE_PATH_FILTER = -16; //根据请求参数method值修改请求uri

    public final static int PRE_SIXTH = -15;
    public final static int PRE_SEVENTH = -14;
    public final static int PRE_EIGHTH = -13;

    public final static int POST_FIRST = -8;
    public final static int POST_SECOND = -7;
    public final static int POST_THIRD = -6;
    public final static int POST_FOURTH = -5;
    public final static int POST_FIFTH = -4;
    public final static int POST_SIXTH = -3;
    public final static int RESPONSE_MODIFY_FILTER = -2;//最后一个过滤器，不能大于-1，否则修改的内容无法生效
}
