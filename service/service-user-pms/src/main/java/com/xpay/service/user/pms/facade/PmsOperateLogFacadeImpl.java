package com.xpay.service.user.pms.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsOperateLog;
import com.xpay.facade.user.pms.service.PmsOperateLogFacade;
import com.xpay.service.user.pms.core.biz.PmsOperateLogBiz;
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
public class PmsOperateLogFacadeImpl implements PmsOperateLogFacade {
    @Autowired
    private PmsOperateLogBiz pmsOperateLogBiz;

    @Override
    public void createOperateLog(PmsOperateLog operateLog) {
        pmsOperateLogBiz.createOperateLog(operateLog);
    }

    @Override
    public PageResult<List<PmsOperateLog>> listOperateLogPage(Map<String, Object> paramMap, PageParam pageParam) {
        return pmsOperateLogBiz.listOperateLogPage(paramMap, pageParam);
    }

    @Override
    public PmsOperateLog getOperateLogById(Long id) {
        return pmsOperateLogBiz.getOperateLogById(id);
    }
}
