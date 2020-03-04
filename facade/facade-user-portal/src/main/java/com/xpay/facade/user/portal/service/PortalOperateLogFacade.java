package com.xpay.facade.user.portal.service;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.portal.entity.PortalOperateLog;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 15:29
 * Description:
 */
public interface PortalOperateLogFacade {

    void createOperateLog(PortalOperateLog operateLog);

    PageResult<List<PortalOperateLog>> listOperateLogPage(Map<String, Object> paramMap, PageParam pageParam);

    PortalOperateLog getOperateLogById(Long id);
}
