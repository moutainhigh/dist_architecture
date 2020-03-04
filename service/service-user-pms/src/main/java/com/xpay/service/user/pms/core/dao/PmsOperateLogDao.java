package com.xpay.service.user.pms.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.user.pms.entity.PmsOperateLog;
import org.springframework.stereotype.Repository;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 15:49
 * Description:
 */
@Repository("pmsOperateLogDao")
public class PmsOperateLogDao extends MyBatisDao<PmsOperateLog, Long> {

}
