package com.xpay.service.user.pms.core.biz;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.user.pms.entity.PmsOperateLog;
import com.xpay.service.user.pms.core.dao.PmsOperateLogDao;
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
public class PmsOperateLogBiz {
    @Autowired
    private PmsOperateLogDao pmsOperateLogDao;

    public void createOperateLog(PmsOperateLog operateLog) {
        pmsOperateLogDao.insert(operateLog);
    }

    public PageResult<List<PmsOperateLog>> listOperateLogPage(Map<String, Object> paramMap, PageParam pageParam) {
        return pmsOperateLogDao.listPage(paramMap, pageParam);
    }

    public PmsOperateLog getOperateLogById(Long id) {
        return pmsOperateLogDao.getById(id);
    }

}
