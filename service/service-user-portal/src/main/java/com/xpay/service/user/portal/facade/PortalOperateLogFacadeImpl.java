package com.xpay.service.user.portal.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalOperateLog;
import com.xpay.facade.user.portal.service.PortalOperateLogFacade;
import com.xpay.service.user.portal.core.biz.PortalOperateLogBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 15:48
 * Description:
 */
@Service
public class PortalOperateLogFacadeImpl implements PortalOperateLogFacade {
    @Autowired
    private PortalOperateLogBiz portalOperateLogBiz;

    @Override
    public void createOperateLog(PortalOperateLog operateLog) {
        portalOperateLogBiz.createOperateLog(operateLog);
    }

    @Override
    public PageResult<List<PortalOperateLog>> listOperateLogPage(Map<String, Object> paramMap, PageParam pageParam) {
        return portalOperateLogBiz.listOperateLogPage(paramMap, pageParam);
    }

    @Override
    public PortalOperateLog getOperateLogById(Long id) {
        return portalOperateLogBiz.getOperateLogById(id);
    }
}
