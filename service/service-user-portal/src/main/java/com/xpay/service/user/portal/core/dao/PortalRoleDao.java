package com.xpay.service.user.portal.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.enums.user.portal.PortalRoleTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.user.portal.entity.PortalRole;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 14:57
 * Description: 角色表数据访问层接口实现类.
 */
@Repository("portalRoleDao")
public class PortalRoleDao extends MyBatisDao<PortalRole, Long> {

    /**
     * 列出所有角色，以供添加操作员时选择.
     *
     * @return roleList .
     */
    public List<PortalRole> listAll() {
        return super.listAll();
    }


    /**
     * 获取某个商户所有的角色
     *
     * @param merchantNo .
     * @return .
     */
    public List<PortalRole> listRoleByMerchantNo(String merchantNo) {
        if (StringUtil.isEmpty(merchantNo)) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("merchantNo不能为空");
        }
        return super.listBy(Collections.singletonMap("merchantNo", merchantNo));
    }


    /**
     * 获取所有的商户管理员角色
     *
     * @return .
     */
    public List<PortalRole> listAllAdminRoles() {
        return super.listBy(Collections.singletonMap("type", PortalRoleTypeEnum.PORTAL_ADMIN.getValue()));
    }

    /**
     * 根据角色名称获取角色记录（用于判断角色名是否已存在）.
     *
     * @param roleName 角色名.
     * @return PortalRole.
     */
    public PortalRole getByRoleName(String roleName) {
        if (roleName == null) {
            return null;
        }
        return super.getOne(Collections.singletonMap("roleName", roleName));
    }

    /**
     * 根据权限ID找出关联了此权限的角色.
     *
     * @param actionId .
     * @return roleList.
     */
    public List<PortalRole> listByActionId(Long actionId) {
        return super.listBy("listByActionId", Collections.singletonMap("actionId", actionId));
    }


}
