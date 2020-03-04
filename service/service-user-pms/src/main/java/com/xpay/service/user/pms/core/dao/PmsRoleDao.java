package com.xpay.service.user.pms.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.user.pms.entity.PmsRole;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 14:57
 * Description: 角色表数据访问层接口实现类.
 */
@Repository("pmsRoleDao")
public class PmsRoleDao extends MyBatisDao<PmsRole, Long> {

    /**
     * 列出所有角色，以供添加操作员时选择.
     *
     * @return roleList .
     */
    public List<PmsRole> listAll() {
        return super.listAll();
    }

    /**
     * 根据角色名称获取角色记录（用于判断角色名是否已存在）.
     *
     * @param roleName 角色名.
     * @return PmsRole.
     */
    public PmsRole getByRoleName(String roleName) {
        if (roleName == null) {
            return null;
        }
        return super.getOne(Collections.singletonMap("roleName", roleName));
    }
}
