package com.xpay.facade.user.pms.service;


import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsOperator;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:14
 * Description:
 */
public interface PmsOperatorFacade {

    PmsOperator getOperatorById(long id);

    PmsOperator getOperatorByLoginName(String loginName);

    void deleteOperatorById(long id);

    List<PmsOperator> listOperatorByRoleId(long roleId);

    void createOperator(PmsOperator operator);

    void updateOperator(PmsOperator operator);

    void updateOperatorAndAssignRoles(PmsOperator operator, List<Long> roleIds);

	void insertOperatorAndAssignRoles(PmsOperator operator, List<Long> roleIds);

    void updateOperatorPwd(Long operatorId, String newPwd, boolean isChangedPwd);

    PageResult<List<PmsOperator>> listOperatorPage(Map<String, Object> paramMap, PageParam pageParam);
}
