package com.xpay.service.user.pms.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.user.pms.entity.PmsRoleOperator;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 11:46
 * Description:
 */
@Repository
public class PmsRoleOperatorDao extends MyBatisDao<PmsRoleOperator, Long> {

    /**
     * 根据操作员ID查找该操作员关联的角色.
     *
     * @param operatorId .
     * @return list .
     */
    public List<PmsRoleOperator> listByOperatorId(Long operatorId) {
        if (operatorId == null) {
            return new ArrayList<>();
        }
        return super.listBy(Collections.singletonMap("operatorId", operatorId));
    }

    /**
     * 根据角色ID查找该操作员关联的操作员.
     *
     * @param roleId
     * @return
     */
    public List<PmsRoleOperator> listByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return super.listBy(Collections.singletonMap("roleId", roleId));

    }

    /**
     * 根据操作员ID删除与角色的关联记录.
     *
     * @param operatorId .
     */
    public void deleteByOperatorId(long operatorId) {
        super.deleteBy("deleteByOperatorId", operatorId);
    }

    /**
     * 根据角色ID删除操作员与角色的关联关系.
     *
     * @param roleId .
     */
    public void deleteByRoleId(long roleId) {
        super.deleteBy("deleteByRoleId", roleId);
    }

    /**
     * 根据角色ID和操作员ID删除关联数据(用于更新操作员的角色).
     *
     * @param roleId     角色ID.
     * @param operatorId 操作员ID.
     */
    public void deleteByRoleIdAndOperatorId(long roleId, long operatorId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("roleId", roleId);
        paramMap.put("operatorId", operatorId);
        super.deleteBy("deleteByRoleIdAndOperatorId", paramMap);
    }


}
