package com.xpay.service.migration.biz;

import com.xpay.common.statics.dto.migrate.MigrateParamDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.starter.comp.component.TaskExecutorPool;
import com.xpay.starter.generic.hepler.GlobalLockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description:
 * @author: chenyf
 * @Date: 2018/3/24
 */
@Component
public class DataMigrateHelper {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private TaskExecutorPool taskExecutorPool;

    @Autowired
    private GlobalLockHelper globalLockHelper;


    public final void doMigration(MigrateParamDto migrateParam) {
        logger.info("数据迁移,MigrateParamDto={}", JsonUtil.toString(migrateParam));

        paramValid(migrateParam);
        String resourceId = "dataMigration-" + migrateParam.getMigratorName();
        String clientId = null;
        try {
            clientId = globalLockHelper.tryLock(resourceId, 2 * 60 * 60, "service-migration");
            if (StringUtil.isEmpty(clientId)) {
                logger.info("获取锁失败，当前任务可能已经在执行,resourceId={}", resourceId);
            } else {
                final String finalClientId = clientId;
                addTask(migrateParam, () -> globalLockHelper.unlock(resourceId, finalClientId));
            }
        } catch (Exception ex) {
            logger.error("添加数据迁移任务时出现异常,resourceId={},clientId={}", resourceId, clientId, ex);
        }
    }

    private void paramValid(MigrateParamDto migrateParam) {
        if (migrateParam == null) {
            throw new BizException(BizException.MQ_DISCARDABLE, "migrateParam为空");
        } else if (StringUtil.isEmpty(migrateParam.getMigratorName())) {
            throw new BizException(BizException.MQ_DISCARDABLE, "migratorName为空");
        } else if (migrateParam.getMigrateTypeArr() == null || migrateParam.getMigrateTypeArr().length <= 0) {
            throw new BizException(BizException.MQ_DISCARDABLE, "MmgrateTypeArr为空");
        } else if (migrateParam.getConcurrent() <= 0) {
            throw new BizException(BizException.MQ_DISCARDABLE, "concurrent不能小于1");
        } else if (migrateParam.getMigrateSecond() <= 0) {
            throw new BizException(BizException.MQ_DISCARDABLE, "migrateSecond不能小于1");
        }
    }

    private void addTask(MigrateParamDto migrateParam, Runnable callback) {
        DataMigrator migrator;
        try {
            migrator = beanFactory.getBean(migrateParam.getMigratorName(), DataMigrator.class);
        } catch (Exception ex) {
            logger.error("migratorName={} 迁移器不存在", migrateParam.getMigratorName());
            return;
        }

        List<DataMigrateTypeEnum> typeList = new ArrayList<>();

        Arrays.stream(migrateParam.getMigrateTypeArr()).forEach(migrateTypeName -> {
            DataMigrateTypeEnum migrateType = DataMigrateTypeEnum.getEnum(migrateTypeName);
            if (migrateType == null) {
                logger.warn("migrateType={} 没有匹配到对应的DataMigrateType枚举值，将忽略", migrateTypeName);
            } else {
                typeList.add(migrateType);
            }
        });

        if (typeList.isEmpty()) {
            logger.info("migratorName={} 没有任何匹配的DataMigrateType，将直接返回", migrateParam.getMigratorName());
            return;
        }

        AtomicInteger count = new AtomicInteger(typeList.size());
        Semaphore semaphore = new Semaphore(migrateParam.getConcurrent());//控制并发量
        typeList.forEach(migrateType -> {
            taskExecutorPool.execute(() -> {
                boolean acquired = false;
                try {
                    semaphore.acquire();
                    acquired = true;
                    migrator.startDataMigration(migrateType);
                } catch (Exception e) {
                    logger.error("migrateType = {} 任务执行过程中出现异常", migrateType, e);
                } finally {
                    if (acquired) {
                        semaphore.release();
                    }
                    if (count.decrementAndGet() <= 0) {
                        callback.run();
                    }
                }
            });
        });
    }
}
