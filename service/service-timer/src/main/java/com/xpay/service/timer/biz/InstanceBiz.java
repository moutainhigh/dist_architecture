package com.xpay.service.timer.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.timer.entity.Namespace;
import com.xpay.facade.timer.enums.OpTypeEnum;
import com.xpay.facade.timer.enums.TimerStatus;
import com.xpay.service.timer.dao.InstanceDao;
import com.xpay.service.timer.dao.NamespaceDao;
import com.xpay.service.timer.job.base.JobManager;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.xpay.facade.timer.entity.Instance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 实例状态逻辑层
 */
@Component
public class InstanceBiz {
    /**
     * 是否已完成初始化
     */
    private boolean isInitFinished = false;
    /**
     * 是否处于运行状态
     */
    private boolean isRunning = true;
    @Autowired
    OpLogBiz opLogBiz;
    @Autowired
    JobManager jobManager;
    @Autowired
    InstanceDao instanceDao;
    @Autowired
    NamespaceDao namespaceDao;

    /**
     * 从namespace同步状态到实例
     *
     * @param namespace
     * @param namespaceStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean syncStatusFromNamespace(String namespace, int namespaceStatus, String operator) {
        boolean isOk = false;
        if (namespaceStatus == TimerStatus.STAND_BY.getValue()) {
            isOk = pause(namespace, operator);
        } else if (namespaceStatus == TimerStatus.RUNNING.getValue()) {
            isOk = resume(namespace, operator);
        }

        if (isOk && !isInitFinished()) {//设置初始化完成的标识
            setInitFinished();
        }
        return isOk;
    }

    /**
     * 当前实例是否已完成初始化
     *
     * @return
     */
    public boolean isInitFinished() {
        return isInitFinished;
    }

    /**
     * 判断当前实例所在的namespace，是否全部实例都处于暂停中
     */
    public boolean isAllInstancePausing(String namespace) {
        if (StringUtil.isEmpty(namespace)) {
            throw new BizException("namespace不能为空");
        }

        List<Instance> instanceList = instanceDao.listByNamespace(namespace);
        if (instanceList.isEmpty()) {
            return false;
        }

        boolean isPausing = true;
        for (Instance stage : instanceList) {
            if (stage.getStatus() != TimerStatus.STAND_BY.getValue()) {
                isPausing = false;
                break;
            }
        }
        return isPausing;
    }

    /**
     * 判断当前实例所在的namespace，是否全部实例都处于运行
     */
    public boolean isAllInstanceRunning(String namespace) {
        if (StringUtil.isEmpty(namespace)) {
            throw new BizException("namespace不能为空");
        }

        List<Instance> instanceList = instanceDao.listByNamespace(namespace);
        if (instanceList.isEmpty()) {
            return false;
        }

        boolean isRunning = true;
        for (Instance stage : instanceList) {
            if (stage.getStatus() != TimerStatus.RUNNING.getValue()) {
                isRunning = false;
                break;
            }
        }
        return isRunning;
    }

    public PageResult<List<Instance>> listInstancePage(Map<String, Object> paramMap, PageParam pageParam) {
        PageResult<List<Instance>> pageResult = instanceDao.listPage(paramMap, pageParam);
        if (pageResult.getData() != null && pageResult.getData().size() > 0) {
            //一般情况下namespace也就只有几个
            PageResult<Map<String, Namespace>> result = namespaceDao.mapByIdPage(null, PageParam.newInstance(1, 200));
            Map<String, Namespace> namespaceMap = result.getData();
            for (Instance instance : pageResult.getData()) {
                if (namespaceMap != null && namespaceMap.containsKey(instance.getNamespace())) {
                    instance.setNamespaceStatus(namespaceMap.get(instance.getNamespace()).getStatus());
                    instance.setNamespaceName(namespaceMap.get(instance.getNamespace()).getNameCn());
                }
            }
        }
        return pageResult;
    }

    public Instance getByInstanceId(String instanceId) {
        return instanceDao.getByInstanceId(instanceId);
    }

    public boolean addInstance(String instanceId, String namespace, Integer status, String operator) {
        Instance instance = new Instance();
        instance.setInstanceId(instanceId);
        instance.setNamespace(namespace);
        instance.setStatus(status);
        instance.setUpdateTime(instance.getCreateTime());
        try {
            instanceDao.insert(instance);
        } catch (Exception ex) {
            throw new BizException("instanceId = " + instanceId + " 当前实例初始化失败");
        }

        opLogBiz.addAsync(operator, instanceId, OpTypeEnum.ADD, "新增实例");
        return true;
    }

    public boolean updateCheckInTime(String instanceId) {
        return instanceDao.updateCheckInTime(instanceId);
    }

    public String getInstanceId() {
        try {
            return jobManager.getSchedulerInstanceId();
        } catch (SchedulerException e) {
            throw new BizException("获取实例Id异常", e);
        }
    }

    /**
     * 暂停当前实例：需要确保事务的一致性
     *
     * @param namespace
     * @return
     */
    private boolean pause(String namespace, String operator) {
        //如果已经处理处于暂停状态，则直接返回(为了降低数据库压力)
        if (!isRunning) {
            return true;
        }

        //更新实例记录的状态为'挂起中'，同时挂起Quartz引擎的触发器执行线程
        String instanceId = getInstanceId();
        boolean isOk = instanceDao.updateInstanceStatus(TimerStatus.STAND_BY.getValue(), instanceId, namespace);
        if (!isOk) {
            throw new BizException("unexpect error, instanceId=" + instanceId + " set to pause fail！");
        }
        if (!jobManager.isStandByMode()) {
            jobManager.pauseInstance();
        }
        setToPausing();//为确保状态一致性，需在 本地记录更新完毕、Quartz引擎修改完成 这两步之后执行

        opLogBiz.addAsync(operator, instanceId, OpTypeEnum.PAUSE, "从分区同步'挂起'状态");

        return true;
    }

    /**
     * 恢复当前实例：需要确保事务的一致性
     *
     * @param namespace
     * @return
     */
    private boolean resume(String namespace, String operator) {
        //如果已经处理处于运行状态，则直接返回(为了降低数据库压力)
        if (isRunning) {
            return true;
        }

        //更新实例记录的状态为'挂起中'，同时挂起Quartz引擎的触发器执行线程
        String instanceId = getInstanceId();
        boolean isOk = instanceDao.updateInstanceStatus(TimerStatus.RUNNING.getValue(), instanceId, namespace);
        if (!isOk) {
            throw new BizException("unexpect error, instanceId=" + instanceId + " resume to running fail！");
        }
        if (jobManager.isStandByMode()) {
            jobManager.resumeInstance();
        }
        setToRunning();//为确保状态一致性，需在 本地记录更新完毕、Quartz引擎修改完成 这两步之后执行

        opLogBiz.addAsync(operator, instanceId, OpTypeEnum.RESUME, "从分区同步'运行'状态");

        return true;
    }

    private synchronized void setToRunning() {
        isRunning = true;
    }

    private synchronized void setToPausing() {
        isRunning = false;
    }

    private synchronized void setInitFinished() {
        isInitFinished = true;
    }
}
