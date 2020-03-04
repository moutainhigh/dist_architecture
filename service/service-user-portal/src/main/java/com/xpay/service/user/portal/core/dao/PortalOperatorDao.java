package com.xpay.service.user.portal.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.user.portal.entity.PortalOperator;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 14:50
 * Description: 操作员表数据访问层接口实现类
 */
@Repository("portalOperatorDao")
public class PortalOperatorDao extends MyBatisDao<PortalOperator, Long> {
    /**
     * 根据操作员登录名获取操作员信息.
     *
     * @param loginName .
     * @return operator .
     */
    public PortalOperator findByLoginName(String loginName) {
        if (StringUtil.isEmpty(loginName)) {
            return null;
        }
        return getOne(Collections.singletonMap("loginName", loginName));
    }

    public List<PortalOperator> listByRoleId(long roleId) {
        return super.listBy("listByRoleId", Collections.singletonMap("roleId", roleId));
    }


}
