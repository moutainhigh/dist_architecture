package com.xpay.service.user.pms.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.facade.user.pms.service.PmsOperatorFacade;
import com.xpay.service.user.pms.core.biz.PmsOperatorBiz;
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
public class PmsOperatorFacadeImpl implements PmsOperatorFacade {

    @Autowired
    private PmsOperatorBiz pmsOperatorBiz;

    @Override
    public PmsOperator getOperatorById(long id) {
        return pmsOperatorBiz.getOperatorById(id);
    }

    @Override
    public PmsOperator getOperatorByLoginName(String loginName) {
        return pmsOperatorBiz.getOperatorByLoginName(loginName);
    }

    @Override
    public void deleteOperatorById(long id) {
        pmsOperatorBiz.deleteOperatorById(id);
    }

    @Override
    public List<PmsOperator> listOperatorByRoleId(long roleId) {
        return pmsOperatorBiz.listOperatorByRoleId(roleId);
    }

    @Override
    public void createOperator(PmsOperator operator) {
		pmsOperatorBiz.createOperator(operator);
    }

    @Override
    public void updateOperator(PmsOperator operator) {
        pmsOperatorBiz.updateOperator(operator);
    }

    @Override
    public void updateOperatorAndAssignRoles(PmsOperator operator, List<Long> roleIds) {
        pmsOperatorBiz.updateOperatorAndAssignRoles(operator, roleIds);
    }
	@Override
	public void insertOperatorAndAssignRoles(PmsOperator operator, List<Long> roleIds) {
		pmsOperatorBiz.insertOperatorAndAssignRoles(operator, roleIds);
	}

    @Override
    public void updateOperatorPwd(Long operatorId, String newPwd, boolean isChangedPwd) {
        pmsOperatorBiz.updateOperatorPwd(operatorId, newPwd, isChangedPwd);

    }

    @Override
    public PageResult<List<PmsOperator>> listOperatorPage(Map<String, Object> paramMap, PageParam pageParam) {
        return pmsOperatorBiz.listOperatorPage(paramMap, pageParam);
    }
}
