package com.xpay.service.user.portal.core.biz;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalOperateLog;
import com.xpay.service.user.portal.core.dao.PortalOperateLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 15:49
 * Description:
 */
@Service
public class PortalOperateLogBiz {
    @Autowired
    private PortalOperateLogDao portalOperateLogDao;

    public void createOperateLog(PortalOperateLog operateLog) {
        portalOperateLogDao.insert(operateLog);
    }

    public PageResult<List<PortalOperateLog>> listOperateLogPage(Map<String, Object> paramMap, PageParam pageParam) {
        return portalOperateLogDao.listPage(paramMap, pageParam);
    }

    public PortalOperateLog getOperateLogById(Long id) {
        return portalOperateLogDao.getById(id);
    }

}
