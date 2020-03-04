package com.xpay.service.user.portal.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.user.portal.entity.PortalOperateLog;
import org.springframework.stereotype.Repository;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 15:49
 * Description:
 */
@Repository("portalOperateLogDao")
public class PortalOperateLogDao extends MyBatisDao<PortalOperateLog, Long> {

}
