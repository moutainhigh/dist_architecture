package com.xpay.service.user.portal.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.facade.user.portal.entity.PortalFunction;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/22
 * Time: 20:22
 * Description:
 */
@Repository("portalFunctionDao")
public class PortalFunctionDao extends MyBatisDao<PortalFunction, Long> {
    public List<PortalFunction> listByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.size() == 0) {
            return Collections.emptyList();
        }
        return super.listBy("listByRoleIds", Collections.singletonMap("roleIds", roleIds));
    }

    public List<PortalFunction> listByParentId(Long parentId) {
        if (parentId == null) {
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("parentId不能为null");
        }
        return super.listBy("listByParentId", Collections.singletonMap("parentId", parentId));
    }
}
