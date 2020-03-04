package com.xpay.service.user.pms.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.user.pms.entity.PmsOperator;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/10
 * Time: 14:50
 * Description: 操作员表数据访问层接口实现类
 */
@Repository("pmsOperatorDao")
public class PmsOperatorDao extends MyBatisDao<PmsOperator, Long> {

    /**
     * 根据操作员登录名获取操作员信息.
     *
     * @param loginName     登录名
     */
    public PmsOperator findByLoginName(String loginName) {
        if (StringUtil.isEmpty(loginName)) {
            return null;
        }
        return getOne(Collections.singletonMap("loginName", loginName));
    }

    /**
     * 查询角色关联的操作员
     * @param roleId    角色id
     */
    public List<PmsOperator> listByRoleId(long roleId) {
        return super.listBy("listByRoleId", Collections.singletonMap("roleId", roleId));
    }
}
