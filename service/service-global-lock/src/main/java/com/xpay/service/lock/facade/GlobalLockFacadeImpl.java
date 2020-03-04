package com.xpay.service.lock.facade;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.lock.entity.GlobalLock;
import com.xpay.facade.lock.service.GlobalLockFacade;
import com.xpay.service.lock.biz.GlobalLockBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2020.1.15
 * Time: 11:44
 * Description:
 */
@Service
public class GlobalLockFacadeImpl implements GlobalLockFacade {
    @Autowired
    private GlobalLockBiz globalLockBiz;

    @Override
    public String tryLock(String resourceId, int expireSecond, String clientFlag) throws BizException {
        return globalLockBiz.tryLock(resourceId, expireSecond, clientFlag);
    }

    @Override
    public boolean unlock(String resourceId, String clientId) throws BizException {
        return globalLockBiz.unlock(resourceId, clientId);
    }

    @Override
    public boolean unlockForce(String resourceId, boolean isNeedDelete) throws BizException {
        return globalLockBiz.unlockForce(resourceId, isNeedDelete);
    }

    @Override
    public PageResult<List<GlobalLock>> listPage(Map<String, Object> paramMap, PageParam pageParam) {
        return globalLockBiz.listPage(paramMap, pageParam);
    }
}
