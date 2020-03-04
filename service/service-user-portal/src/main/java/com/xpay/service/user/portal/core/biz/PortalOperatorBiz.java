package com.xpay.service.user.portal.core.biz;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalOperator;
import com.xpay.facade.user.portal.entity.PortalRoleOperator;
import com.xpay.service.user.portal.core.dao.PortalOperatorDao;
import com.xpay.service.user.portal.core.dao.PortalRoleOperatorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019/10/11
 * Time: 15:53
 * Description:
 */
@Service
public class PortalOperatorBiz {
    public static final int HISTORY_PWD_MAX_COUNT = 4;


    @Autowired
    private PortalOperatorDao portalOperatorDao;

    @Autowired
    private PortalRoleOperatorDao portalRoleOperatorDao;

    public PortalOperator getOperatorById(Long id) {
        return portalOperatorDao.getById(id);
    }


    public PortalOperator getOperatorByLoginName(String loginName) {
        return portalOperatorDao.findByLoginName(loginName);
    }

    public void updateOperator(PortalOperator portalOperator) {
        portalOperatorDao.update(portalOperator);
    }

    public void updateOperatorPwd(Long operatorId, String newPwd, boolean isChangedPwd) {
        PortalOperator portalOperator = portalOperatorDao.getById(operatorId);
        portalOperator.setLoginPwd(newPwd);
        portalOperatorDao.update(portalOperator);
    }

    public List<PortalOperator> listOperatorByRoleId(long roleId) {
        return portalOperatorDao.listByRoleId(roleId);
    }

    public PageResult<List<PortalOperator>> listOperatorPage(Map<String, Object> paramMap, PageParam pageParam) {
        return portalOperatorDao.listPage(paramMap, pageParam);
    }

    @Transactional
    public void deleteOperatorById(long id) {
        portalOperatorDao.deleteById(id);
        portalRoleOperatorDao.deleteByOperatorId(id);
    }

    @Transactional
    public void updateOperatorAndAssignRoles(PortalOperator operator, List<Long> roleIds) {
        portalOperatorDao.update(operator);
        portalRoleOperatorDao.deleteByOperatorId(operator.getId());
        List<PortalRoleOperator> roleOperatorList = roleIds.stream().map(p -> {
            PortalRoleOperator portalRoleOperator = new PortalRoleOperator();
            portalRoleOperator.setRoleId(p);
            portalRoleOperator.setOperatorId(operator.getId());
            return portalRoleOperator;
        }).collect(Collectors.toList());
        portalRoleOperatorDao.insert(roleOperatorList);
    }
}
