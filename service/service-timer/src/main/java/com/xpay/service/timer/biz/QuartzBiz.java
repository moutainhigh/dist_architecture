package com.xpay.service.timer.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.timer.enums.OpTypeEnum;
import com.xpay.service.timer.dao.ScheduleJobDao;
import com.xpay.service.timer.job.base.JobManager;
import com.xpay.service.timer.job.base.JobNotifier;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.xpay.facade.timer.entity.ScheduleJob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyf on 2017/8/26.
 */
@Component
public class QuartzBiz {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ScheduleJobDao scheduleJobDao;
    @Autowired
    JobNotifier jobNotifier;
    @Autowired
    JobManager jobManager;
    @Autowired
    OpLogBiz opLogBiz;

    /**
     * 直接触发任务的消息通知，实例处于挂起状态中，但需要对个别任务触发时使用（实行 蓝绿发布 策略时可能会有用）
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean sendJobNotify(String jobGroup, String jobName, String operator) {
        ScheduleJob scheduleJob = scheduleJobDao.getByName(jobGroup, jobName);
        if (scheduleJob == null) {
            throw new BizException(BizException.BIZ_INVALIDATE, "任务不存在");
        }

        boolean isOk = jobNotifier.notify(scheduleJob);
        if(isOk){
            logger.info("jobGroup={} jobName={} 已通知成功", jobGroup, jobName);
            opLogBiz.addAsync(operator, jobGroup, jobName, OpTypeEnum.NOTIFY, "发起任务通知");
        }
        return isOk;
    }

    /**
     * 添加一个定时任务
     *
     * @param scheduleJob
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long add(ScheduleJob scheduleJob, String operator) {
        this.checkJobParam(scheduleJob);
        this.initScheduleJob(scheduleJob);

        try {
            scheduleJobDao.insert(scheduleJob);
            Date startTime = jobManager.addJob(scheduleJob);
            //还需要再次更新ScheduleJob，因为addJob方法内部会设置scheduleJob的jobStatus、nextExecuteTime等
            if (startTime != null) {
                scheduleJobDao.updateIfNotNull(scheduleJob);
                opLogBiz.addAsync(operator, scheduleJob.getJobGroup(), scheduleJob.getJobName(), OpTypeEnum.ADD, "新增任务，ScheduleJob = " + JsonUtil.toString(scheduleJob));
                return scheduleJob.getId();
            } else {
                //抛出异常让事务回滚
                throw new BizException("添加任务失败");
            }
        } catch (BizException e) {
            throw e;
        } catch (Throwable e) {
            logger.error("添加任务时出现异常 ScheduleJob = {} ", JsonUtil.toString(scheduleJob), e);
            throw new BizException("添加任务发生异常", e);
        }
    }

    /**
     * 重新安排定时任务的定时规则
     *
     * @param scheduleJob
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean rescheduleJob(ScheduleJob scheduleJob, String operator) {
        if (scheduleJob == null) {
            throw new BizException("scheduleJob为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobGroup())) {
            throw new BizException("jobGroup为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobName())) {
            throw new BizException("jobName为空");
        }

        ScheduleJob scheduleJobTemp = scheduleJobDao.getByName(scheduleJob.getJobGroup(), scheduleJob.getJobName());
        String modifyContent = new String("OldScheduleJob = " + JsonUtil.toString(scheduleJobTemp));
        if (scheduleJobTemp == null) {
            throw new BizException("任务不存在");
        }

        //设置允许更新的属性值
        if (scheduleJobTemp.getJobType().intValue() == ScheduleJob.SIMPLE_JOB) {
            if (scheduleJob.getIntervals() != null) {
                scheduleJobTemp.setIntervals(scheduleJob.getIntervals());
            }
            if (scheduleJob.getIntervalUnit() != null) {
                scheduleJobTemp.setIntervalUnit(scheduleJob.getIntervalUnit());
            }
            if (scheduleJob.getRepeatTimes() != null) {
                scheduleJobTemp.setRepeatTimes(scheduleJob.getRepeatTimes());
            }
        } else if (scheduleJobTemp.getJobType().intValue() == ScheduleJob.CRON_JOB) {
            if (StringUtil.isNotEmpty(scheduleJob.getCronExpression())) {
                scheduleJobTemp.setCronExpression(scheduleJob.getCronExpression());
            }
        }
        if (StringUtil.isNotEmpty(scheduleJob.getDestination())) {
            scheduleJobTemp.setDestination(scheduleJob.getDestination());
        }
        if (scheduleJob.getMqType() != null) {
            scheduleJobTemp.setMqType(scheduleJob.getMqType());
        }
        if (scheduleJob.getEndTime() != null) {
            scheduleJobTemp.setEndTime(scheduleJob.getEndTime());
        }
        if (StringUtil.isNotEmpty(scheduleJob.getParamJson())) {
            scheduleJobTemp.setParamJson(scheduleJob.getParamJson());
        }
        if (StringUtil.isNotEmpty(scheduleJob.getJobDescription())) {
            scheduleJobTemp.setJobDescription(scheduleJob.getJobDescription());
        }

        try {
            scheduleJobDao.update(scheduleJobTemp);
            //执行更新
            Date startTime = jobManager.rescheduleJob(scheduleJobTemp);
            if (startTime != null) {
                opLogBiz.addAsync(operator, scheduleJob.getJobGroup(), scheduleJob.getJobName(), OpTypeEnum.EDIT, "修改任务，" + modifyContent);
                return true;
            } else {
                //抛出异常让事务回滚
                throw new BizException("操作失败");
            }
        } catch (BizException e) {
            throw e;
        } catch (Throwable e) {
            logger.error("重新安排任务时出现异常 ScheduleJob = {} ", JsonUtil.toString(scheduleJob), e);
            throw new BizException("重新安排任务时发生异常", e);
        }
    }

    /**
     * 根据 组名+任务名 暂停定时任务
     *
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean pauseJob(String jobGroup, String jobName, String operator) {
        ScheduleJob scheduleJob = scheduleJobDao.getByName(jobGroup, jobName);
        if (scheduleJob == null) {
            throw new BizException("任务不存在");
        }

        try {
            scheduleJob.setJobStatus(Trigger.TriggerState.PAUSED.name());
            scheduleJobDao.update(scheduleJob);
            jobManager.pauseJob(jobGroup, jobName);
            opLogBiz.addAsync(operator, jobGroup, jobName, OpTypeEnum.PAUSE, "暂停任务");
            return true;
        } catch (BizException e) {
            throw e;
        } catch (Throwable e) {
            logger.error("暂停任务时出现异常 jobGroup={} jobName={} ", jobGroup, jobName, e);
            throw new BizException("暂停任务时发生异常", e);
        }
    }

    /**
     * 根据 组名+任务名 恢复被暂停的定时任务
     *
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean resumeJob(String jobGroup, String jobName, String operator) {
        ScheduleJob scheduleJob = scheduleJobDao.getByName(jobGroup, jobName);
        if (scheduleJob == null) {
            throw new BizException("任务不存在");
        }

        try {
            String status = jobManager.resumeJob(jobGroup, jobName);
            if (StringUtil.isEmpty(status)) {
                return false;
            }

            scheduleJob.setJobStatus(status);
            scheduleJobDao.update(scheduleJob);
            opLogBiz.addAsync(operator, jobGroup, jobName, OpTypeEnum.RESUME, "恢复任务");
            return true;
        } catch (BizException e) {
            throw e;
        } catch (Throwable e) {
            logger.error("恢复任务时出现异常 jobGroup={} jobName={} ", jobGroup, jobName, e);
            throw new BizException("恢复任务时发生异常", e);
        }
    }

    /**
     * 根据 组名+任务名 立即执行一次定时任务，若实例处于挂起状态，则操作无效
     *
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean triggerJob(String jobGroup, String jobName, String operator) {
        ScheduleJob scheduleJob = scheduleJobDao.getByName(jobGroup, jobName);
        if (scheduleJob == null) {
            throw new BizException("任务不存在");
        }

        if(jobManager.isStandByMode()){
            throw new BizException("当前实例挂起中，不可触发任务");
        }

        try {
            jobManager.triggerJob(jobGroup, jobName);
            opLogBiz.addAsync(operator, jobGroup, jobName, OpTypeEnum.EXE, "触发任务");
            return true;
        } catch (BizException e) {
            throw e;
        } catch (Throwable e) {
            logger.error("触发任务时出现异常 jobGroup={} jobName={} ", jobGroup, jobName, e);
            throw new BizException("触发任务时发生异常", e);
        }
    }

    /**
     * 根据 组名+任务名 删除定时任务
     *
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String jobGroup, String jobName, String operator) {
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getByName(jobGroup, jobName);
            if (scheduleJob != null) {
                scheduleJobDao.deleteById(scheduleJob.getId());
            }
            boolean isOk = jobManager.deleteJob(jobGroup, jobName);
            if (isOk) {
                opLogBiz.addAsync(operator, jobGroup, jobName, OpTypeEnum.DEL, "删除任务，ScheduleJob = " + JsonUtil.toString(scheduleJob));
                return true;
            } else {
                //抛出异常让事务回滚
                throw new BizException("删除任务失败");
            }
        } catch (BizException e) {
            throw e;
        } catch (Throwable e) {
            logger.error("删除任务时出现异常 jobGroup={} jobName={} ", jobGroup, jobName, e);
            throw new BizException("删除任务时发生异常", e);
        }
    }

    /**
     * 根据 组名+任务名 取得定时任务
     *
     * @param jobGroup
     * @param jobName
     * @return
     */
    public ScheduleJob getJobByName(String jobGroup, String jobName) {
        return scheduleJobDao.getByName(jobGroup, jobName);
    }

    public ScheduleJob getJobById(long id) {
        return scheduleJobDao.getById(id);
    }

    /**
     * job被触发并执行完毕之后调用的方法，主要用来同步ScheduleJob一些属性，如：jobStatus、lastExecuteTime、nextExecuteTime、executedTimes等等
     *
     * @param jobGroup
     * @param jobName
     * @param jobProperties
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateScheduleJobAfterExecuted(String jobGroup, String jobName, Map<String, Object> jobProperties) {
        if (jobProperties == null) {
            jobProperties = new HashMap<String, Object>(2);
        }
        jobProperties.put("jobGroup", jobGroup);
        jobProperties.put("jobName", jobName);
        return scheduleJobDao.update("updateScheduleJobAfterExecuted", jobProperties) > 0;
    }

    /**
     * job在被检测到misfire之后调用的方法，主要用来同步ScheduleJob一些属性，如：jobStatus、lastExecuteTime、nextExecuteTime等等
     *
     * @param jobGroup
     * @param jobName
     * @param jobProperties
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateScheduleJobAfterMisfire(String jobGroup, String jobName, Map<String, Object> jobProperties) {
        if (jobProperties == null) {
            jobProperties = new HashMap<String, Object>(2);
        }
        jobProperties.put("jobGroup", jobGroup);
        jobProperties.put("jobName", jobName);
        return scheduleJobDao.update("updateScheduleJobAfterMisfire", jobProperties) > 0;
    }

    /**
     * 分页查询SchduleJob
     *
     * @param paramMap
     * @param pageParam
     * @return
     */
    public PageResult<List<ScheduleJob>> listPage(Map<String, Object> paramMap, PageParam pageParam) {
        return scheduleJobDao.listPage(paramMap, pageParam);
    }

    /**
     * 初始化ScheduleJob，把一些必填的属性给赋上默认值
     *
     * @param scheduleJob
     */
    private void initScheduleJob(ScheduleJob scheduleJob) {
        if (scheduleJob.getExecutedTimes() == null) {
            scheduleJob.setExecutedTimes(0L);
        }
    }

    private void checkJobParam(ScheduleJob scheduleJob) {
        if (scheduleJob == null) {
            throw new BizException(BizException.PARAM_INVALIDATE, "scheduleJob不能为空");
        } else if (scheduleJob.getJobType() == null) {
            throw new BizException(BizException.PARAM_INVALIDATE, "任务类型(jobType)不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobGroup())) {
            throw new BizException(BizException.PARAM_INVALIDATE, "任务的组名(jobGroup)不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobName())) {
            throw new BizException(BizException.PARAM_INVALIDATE, "任务名(jobName)不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getDestination())) {
            throw new BizException(BizException.PARAM_INVALIDATE, "任务通知目的地(destination)不能为空");
        } else if (scheduleJob.getMqType() == null) {
            throw new BizException(BizException.PARAM_INVALIDATE, "MQ类型(mqType)不能为空");
        } else if (scheduleJob.getStartTime() == null) {
            throw new BizException(BizException.PARAM_INVALIDATE, "开始时间(startTime)不能为空");
        }

        if (scheduleJob.getJobType().equals(ScheduleJob.SIMPLE_JOB)) {
            if (scheduleJob.getIntervals() == null) {
                throw new BizException(BizException.PARAM_INVALIDATE, "任务间隔(interval)不能为空");
            } else if (scheduleJob.getIntervalUnit() == null) {
                throw new BizException(BizException.PARAM_INVALIDATE, "任务间隔单位(intervalUnit)不能为空");
            }
        } else if (scheduleJob.getJobType().equals(ScheduleJob.CRON_JOB)) {
            if (StringUtil.isEmpty(scheduleJob.getCronExpression())) {
                throw new BizException(BizException.PARAM_INVALIDATE, "cron表达式(cronExpression)不能为空");
            }
        } else {
            throw new BizException(BizException.PARAM_INVALIDATE, "未支持的任务类型jobType: " + scheduleJob.getJobType());
        }

        if (!scheduleJob.getMqType().equals(ScheduleJob.MQ_TYPE_ROCKET)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "未支持的MQ类型mqType: " + scheduleJob.getMqType());
        }
    }
}
