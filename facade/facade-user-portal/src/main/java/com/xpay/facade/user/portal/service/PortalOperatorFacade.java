package com.xpay.facade.user.portal.service;


import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalOperator;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:14
 * Description:
 */
public interface PortalOperatorFacade {

    PortalOperator getOperatorById(long id);

    PortalOperator getOperatorByLoginName(String loginName);

    void deleteOperatorById(long id);

    List<PortalOperator> listOperatorByRoleId(long roleId);

    void createOperator(PortalOperator operator);

    void updateOperator(PortalOperator operator);

    void updateOperatorAndAssignRoles(PortalOperator operator, List<Long> roleIds);

    void updateOperatorPwd(Long operatorId, String newPwd, boolean isChangedPwd);

    PageResult<List<PortalOperator>> listOperatorPage(Map<String, Object> paramMap, PageParam pageParam);
}
