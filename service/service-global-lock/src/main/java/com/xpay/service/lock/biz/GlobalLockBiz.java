package com.xpay.service.lock.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.MD5Util;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.lock.entity.GlobalLock;
import com.xpay.facade.lock.enums.GlobalLockStatusEnum;
import com.xpay.service.lock.dao.GlobalLockDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author chenyf
 */
@Component
public class GlobalLockBiz {
    private final static long NEVER_EXPIRE_TIMESTAMP_VALUE = -1;
    private Logger logger = LoggerFactory.getLogger(GlobalLockBiz.class);

    @Autowired
    private GlobalLockDao globalLockDao;

    /**
     * 取得资源锁
     *
     * @param resourceId   申请的资源ID
     * @param expireSecond 超时时间 -1表示永不超时
     * @param clientFlag   客户端标识
     * @return 锁ID
     */
    @Transactional
    public String tryLock(String resourceId, int expireSecond, String clientFlag) {
        if (StringUtil.isEmpty(resourceId)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "resourceId不能为空");
        }else if(resourceId.length() > 32){
            throw new BizException(BizException.PARAM_INVALIDATE, "resourceId长度不能超过32");
        } else if (expireSecond <= 0 && expireSecond != NEVER_EXPIRE_TIMESTAMP_VALUE) {
            throw new BizException(BizException.PARAM_INVALIDATE, "expireSecond 值无效");
        } else if (StringUtil.isNotEmpty(clientFlag) && clientFlag.length() > 50) {
            throw new BizException(BizException.PARAM_INVALIDATE, "clientFlag 长度不能超过50");
        }

        GlobalLock lock = globalLockDao.getByResourceId(resourceId);
        if (lock == null) {
            //如果还没有这条资源的记录，则新增一条并获得锁
            lock = new GlobalLock();
            lock.setResourceId(resourceId);
            this.acquireLock(lock, expireSecond, clientFlag);
            try {
                globalLockDao.insert(lock);
                return lock.getClientId();
            } catch (Throwable e) {
                logger.error("resourceId={} 新增锁记录出现异常，即获取锁失败！", resourceId, e);
                //如果有异常，说明新增记录失败，也即获取锁失败（可能是数据库唯一约束冲突或者其他情况）
                return null;
            }
        }

        if (lock.getResourceStatus() == GlobalLockStatusEnum.FREE.getValue()) {
            //此锁当前空闲，当前客户端可以正常获得锁
            this.acquireLock(lock, expireSecond, clientFlag);
        } else if (lock.getExpireTime() == null) {
            //此锁锁定中，并且过期时间为NULL，说明是被锁持有者设置为永不过期，当前客户端不可获得锁
            return null;
        } else if (DateUtil.compare(lock.getExpireTime(), new Date(), Calendar.SECOND) > 0) {
            //此锁锁定中，并且过期时间大于当前时间，说明是锁持有者持有的锁还未过期，当前客户端不可获得锁
            return null;
        } else {
            //此锁锁定中，并且过期时间小于当前时间，说明是锁持有者已过期，当前客户端可以正常获得锁
            this.acquireLock(lock, expireSecond, clientFlag);
        }
        try {
            globalLockDao.update(lock);
            return lock.getClientId();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 解锁
     *
     * @param resourceId 释放的资源ID
     * @param clientId   客户端获取得到的锁ID
     * @return .
     */
    public boolean unlock(String resourceId, String clientId) {
        if (StringUtil.isEmpty(resourceId)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "resourceId不能为空");
        } else if (StringUtil.isEmpty(clientId)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "clientId不能为空");
        }

        GlobalLock lock = globalLockDao.getByResourceId(resourceId);
        if (lock == null) {
            return true;
        }

        if (lock.getResourceStatus().equals(GlobalLockStatusEnum.FREE.getValue())) {
            //此锁当前空闲，没必要再解锁，直接返回true
            return true;
        } else if (!lock.getClientId().equals(clientId)) {
            //此锁当前锁定中，但是传入的clientId跟数据库中的不相等，说明当前解锁者不是锁持有者，所以不能解锁
            return false;
        } else {
            //此锁当前锁定中，并且当前解锁者是锁持有者，可以释放锁
            this.releaseLock(lock);
        }
        try {
            globalLockDao.update(lock);
            return true;
        } catch (Exception ex) {
            logger.error("释放锁过程中数据库更新异常,resourceId={},clientId={}", resourceId, clientId);
            return false;
        }
    }

    /**
     * 强制解锁
     *
     * @param resourceId   .
     * @param isNeedDelete .
     * @return .
     */
    public boolean unlockForce(String resourceId, boolean isNeedDelete) {
        if (StringUtil.isEmpty(resourceId)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "resourceId不能为空");
        }

        GlobalLock lock = globalLockDao.getByResourceId(resourceId);
        if (lock == null) {
            return true;
        }

        if (isNeedDelete) {
            return deleteResourceLock(resourceId);
        } else {
            this.releaseLock(lock);
            try {
                globalLockDao.update(lock);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * 删除资源锁记录
     *
     * @param resourceId .
     * @return .
     */
    public boolean deleteResourceLock(String resourceId) {
        if (StringUtil.isEmpty(resourceId)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "resourceId不能为空");
        }

        GlobalLock lock = globalLockDao.getByResourceId(resourceId);
        if (lock == null) {
            return true;
        }
        try {
            globalLockDao.deleteById(lock.getId());
            return true;
        } catch (Exception ex) {
            logger.info("删除锁时失败,resourceId={}", resourceId, ex);
            return false;
        }
    }

    /**
     * 分页获取数据
     *
     * @param paramMap  .
     * @param pageParam .
     * @return .
     */
    public PageResult<List<GlobalLock>> listPage(Map<String, Object> paramMap, PageParam pageParam) {
        return globalLockDao.listPage(paramMap, pageParam);
    }

    private void acquireLock(GlobalLock lock, int expireSecond, String clientFlag) {
        String clientId = MD5Util.getMD5Hex(UUID.randomUUID().toString());
        Date now = new Date();
        lock.setResourceStatus(GlobalLockStatusEnum.LOCKING.getValue());
        lock.setClientId(clientId);
        lock.setClientFlag(clientFlag);
        lock.setLockTime(now);
        if (expireSecond == NEVER_EXPIRE_TIMESTAMP_VALUE) {
            lock.setExpireTime(null);
        } else {
            lock.setExpireTime(DateUtil.addSecond(now, expireSecond));
        }
    }

    private void releaseLock(GlobalLock lock) {
        lock.setResourceStatus(GlobalLockStatusEnum.FREE.getValue());
        lock.setClientId("");
        lock.setClientFlag("");
        lock.setLockTime(null);
        lock.setExpireTime(null);
    }
}
