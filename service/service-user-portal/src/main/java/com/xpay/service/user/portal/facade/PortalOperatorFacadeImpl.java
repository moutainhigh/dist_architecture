package com.xpay.service.user.portal.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalOperator;
import com.xpay.facade.user.portal.service.PortalOperatorFacade;
import com.xpay.service.user.portal.core.biz.PortalOperatorBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:24
 * Description:
 */
@Service
public class PortalOperatorFacadeImpl implements PortalOperatorFacade {

    @Autowired
    private PortalOperatorBiz portalOperatorBiz;

    @Override
    public PortalOperator getOperatorById(long id) {
        return portalOperatorBiz.getOperatorById(id);
    }

    @Override
    public PortalOperator getOperatorByLoginName(String loginName) {
        return portalOperatorBiz.getOperatorByLoginName(loginName);
    }

    @Override
    public void deleteOperatorById(long id) {
        portalOperatorBiz.deleteOperatorById(id);
    }

    @Override
    public List<PortalOperator> listOperatorByRoleId(long roleId) {
        return portalOperatorBiz.listOperatorByRoleId(roleId);
    }

    @Override
    public void createOperator(PortalOperator operator) {

    }

    @Override
    public void updateOperator(PortalOperator operator) {
        portalOperatorBiz.updateOperator(operator);
    }

    @Override
    public void updateOperatorAndAssignRoles(PortalOperator operator, List<Long> roleIds) {
        portalOperatorBiz.updateOperatorAndAssignRoles(operator, roleIds);
    }

    @Override
    public void updateOperatorPwd(Long operatorId, String newPwd, boolean isChangedPwd) {
        portalOperatorBiz.updateOperatorPwd(operatorId, newPwd, isChangedPwd);

    }

    @Override
    public PageResult<List<PortalOperator>> listOperatorPage(Map<String, Object> paramMap, PageParam pageParam) {
        return portalOperatorBiz.listOperatorPage(paramMap, pageParam);
    }
}
