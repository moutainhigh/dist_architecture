package com.xpay.service.timer.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.facade.timer.entity.Instance;
import com.xpay.facade.timer.entity.Namespace;
import com.xpay.service.timer.config.TimerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 初始化器
 */
@Component
public class Initializer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AtomicBoolean initializeDone = new AtomicBoolean(false);
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);
    @Autowired
    TimerConfig timerConfig;
    @Autowired
    NamespaceBiz namespaceBiz;
    @Autowired
    InstanceBiz instanceBiz;

    @PostConstruct
    public synchronized void initialize(){
        if(! initializeDone.compareAndSet(false, true)){
            return;
        }

        //1.初始化命名空间的记录
        final String operator = "system";
        final String namespaceStr = timerConfig.getNamespace();
        Namespace namespaceObj = namespaceBiz.getByName(namespaceStr);
        if(namespaceObj == null){
            namespaceBiz.addNamespace(namespaceStr, timerConfig.getNameCn(), operator);
        }

        //2.初始化实例的记录
        final String instanceId = instanceBiz.getInstanceId();
        Instance instance = instanceBiz.getByInstanceId(instanceId);
        if(instance == null){
            instanceBiz.addInstance(instanceId, namespaceStr, namespaceObj.getStatus(), operator);
        }

        //3.把命名空间的状态同步到当前实例状态上去
        instanceBiz.syncStatusFromNamespace(namespaceStr, namespaceObj.getStatus(), operator);

        int internal = timerConfig.getStageCheckMills();
        if(internal <= 1000){
            //避免轮训频率过高压垮数据库，如果是希望实现毫秒级的延迟，应该使用zk/redis等组件的发布-订阅模式来处理，因为不想引入太多组件，所以，此处并没有使用这些组件来实现
            throw new BizException("stageCheckMills = " + internal + " 必须大于1000");
        }
        //4.定时扫描命名空间的状态，并把此状态同步到当前实例状态上去
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    Namespace namespace = namespaceBiz.getByName(namespaceStr);
                    instanceBiz.syncStatusFromNamespace(namespace.getNamespace(), namespace.getStatus(), operator);
                }catch(Throwable e){
                    logger.error("instanceId={} 状态检测同步时出现异常", instanceId, e);
                }
            }
        }, internal, internal, TimeUnit.MILLISECONDS);

        //5.定时更新检入时间
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    instanceBiz.updateCheckInTime(instanceId);
                }catch(Throwable e){
                    logger.error("instanceId={} 更新检入时间时出现异常", instanceId, e);
                }
            }
        }, internal, internal, TimeUnit.MILLISECONDS);
    }
}
