package com.xpay.web.pms.contant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Cmf
 * Date: 2019/10/9
 * Time: 17:08
 * Description: 与权限相关的常量
 */
public class PermissionConstant {

    /**
     * logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(PermissionConstant.class);


    public static final String REQUEST_TOKEN_HEADER = "x-token";

    /**
     * 登录操作员的session键名.
     */
    public static final String OPERATOR_SESSION_KEY = "CURRENT_OPERATOR";

    /**
     * 登录操作员拥有的权限集合的session键名.
     */
    public static final String PERMISSION_SESSION_KEY = "CURRENT_PERMISSIONS";

    /**
     * 保存登录验证码的session键名
     */
    public static final String RANDOM_CODE_SESSION_KEY = "RANDOM_CODE_KEY";

    /**
     * 保存登录验证码生成时间的session键名
     */
    public static final String RANDOM_TIME_SESSION_KEY = "RANDOM_TIME_KEY";

    /**
     * 操作员密码连续输错次数限制(默认5).
     */
    public static int WEB_PWD_INPUT_ERROR_LIMIT = 5;


}
