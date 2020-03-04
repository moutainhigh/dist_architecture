package com.xpay.service.user.pms.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.user.pms.entity.PmsRoleFunction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 14:59
 * Description:
 */
@Repository("pmsRoleFunctionDao")
public class PmsRoleFunctionDao extends MyBatisDao<PmsRoleFunction, Long> {

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