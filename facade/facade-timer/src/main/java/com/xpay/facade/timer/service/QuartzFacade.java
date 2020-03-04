package com.xpay.facade.timer.service;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.timer.entity.ScheduleJob;

import java.util.List;
import java.util.Map;

/**
 * @author chenyf on 2017/8/20.
 */
public interface QuartzFacade {

    /**
     * 直接触发任务的消息通知，实例处于挂起状态中，但需要对个别任务触发时使用（实行 蓝绿发布 策略时可能会有用）
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean sendJobNotify(String jobGroup, String jobName, String operator);

    /**
     * 添加任务
     * @param scheduleJob
     * @return
     */
    public Long add(ScheduleJob scheduleJob, String operator) throws BizException;

    /**
     * 重新安排定时任务，即update任务
     * @param scheduleJob
     * @return
     */
    public boolean rescheduleJob(ScheduleJob scheduleJob, String operator) throws BizException;

    /**
     * 删除任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean delete(String jobGroup, String jobName, String operator) throws BizException;

    /**
     * 暂停任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean pauseJob(String jobGroup, String jobName, String operator) throws BizException;

    /**
     * 恢复被暂停的任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean resumeJob(String jobGroup, String jobName, String operator) throws BizException;

    /**
     * 立即触发任务，若实例处于挂起状态，则操作无效
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean triggerJob(String jobGroup, String jobName, String operator) throws BizException;

    public ScheduleJob getJobByName(String jobGroup, String jobName);

    public PageResult<List<ScheduleJob>> listPage(Map<String, Object> paramMap, PageParam pageParam);
}
