package com.xpay.service.user.portal.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.user.portal.entity.PortalRoleFunction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 14:59
 * Description:
 */
@Repository("portalRoleFunctionDao")
public class PortalRoleFunctionDao extends MyBatisDao<PortalRoleFunction, Long> {


    /**
     * 删除角色的所有功能关联
     *
     * @param roleId 角色id
     */
    public void deleteByRoleId(Long roleId) {
        super.deleteBy("deleteByRoleId", roleId);
    }

    public void deleteByFunctionId(Long functionId) {
        super.deleteBy("deleteByFunctionId", functionId);
    }

    public void deleteByFunctionIds(List<Long> functionIds) {
        super.deleteBy("deleteByFunctionIdList", functionIds);
    }
}