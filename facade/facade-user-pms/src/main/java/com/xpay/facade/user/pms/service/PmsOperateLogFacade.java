package com.xpay.facade.user.pms.service;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsOperateLog;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019/11/1
 * Time: 15:29
 * Description:
 */
public interface PmsOperateLogFacade {

    void createOperateLog(PmsOperateLog operateLog);

    PageResult<List<PmsOperateLog>> listOperateLogPage(Map<String, Object> paramMap, PageParam pageParam);

    PmsOperateLog getOperateLogById(Long id);
}
