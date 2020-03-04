package com.xpay.extend.account.helper;

import com.xpay.common.util.utils.StringUtil;
import com.xpay.starter.comp.component.TaskExecutorPool;
import com.xpay.starter.generic.hepler.GlobalLockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: Cmf
 * Date: 2020.1.8
 * Time: 10:03
 * Description:
 */
@Service
public class ScheduleTaskHelper {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskExecutorPool scheduleTaskAddPool;

    @Autowired
    private GlobalLockHelper globalLockHelper;


    /**
     * 获取全局锁，添加定时任务，任务执行完成后释放全局锁
     *
     * @param runnable     定时任务需要执行的内容
     * @param resourceId   需要获取的全局锁资源id
     * @param expireSecond 全局锁过期时间
     */
    public void addScheduleTask(Runnable runnable, String resourceId, int expireSecond) {
        logger.info("添加定时任务==>addScheduleTask resourceId={},expireSecond={}", resourceId, expireSecond);
        String clientFlag = "extend-account";
        String clientId;
        try {
            clientId = globalLockHelper.tryLock(resourceId, expireSecond, clientFlag);
            if (StringUtil.isEmpty(clientId)) {
                logger.info("获取锁失败，当前任务可能已经在执行,resourceId={}", resourceId);
            } else {
                scheduleTaskAddPool.submit(() -> {
                    try {
                        runnable.run();
                    } finally {
                        globalLockHelper.unlock(resourceId, clientId);
                    }
                });
            }
        } catch (Exception ex) {
            logger.error("添加定时任务时出现异常,resourceId={},expireSecond={}", resourceId, expireSecond, ex);
        }
    }
}
