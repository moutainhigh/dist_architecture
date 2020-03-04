package com.xpay.service.timer.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.xpay.service.timer.biz.QuartzBiz;
import com.xpay.facade.timer.entity.ScheduleJob;
import com.xpay.facade.timer.service.QuartzFacade;

import java.util.List;
import java.util.Map;

/**
 * @author chenyf on 2017/8/20.
 */
@Service
public class QuartzFacadeImpl implements QuartzFacade {
    @Autowired
    QuartzBiz quartzBiz;

    /**
     * 直接触发任务的消息通知，实例处于挂起状态中，但需要对个别任务触发时使用（实行 蓝绿发布 策略时可能会有用）
     * @param jobGroup
     * @param jobName
     * @return
     */
    public boolean sendJobNotify(String jobGroup, String jobName, String operator){
        return quartzBiz.sendJobNotify(jobGroup, jobName, operator);
    }

    @Override
    public Long add(ScheduleJob scheduleJob, String operator){
        return quartzBiz.add(scheduleJob, operator);
    }

    @Override
    public boolean rescheduleJob(ScheduleJob scheduleJob, String operator){
        return quartzBiz.rescheduleJob(scheduleJob, operator);
    }

    @Override
    public boolean delete(String jobGroup, String jobName, String operator){
        return quartzBiz.delete(jobGroup, jobName, operator);
    }

    @Override
    public boolean pauseJob(String jobGroup, String jobName, String operator){
        return quartzBiz.pauseJob(jobGroup, jobName, operator);
    }

    @Override
    public boolean resumeJob(String jobGroup, String jobName, String operator){
        return quartzBiz.resumeJob(jobGroup, jobName, operator);
    }

    @Override
    public boolean triggerJob(String jobGroup, String jobName, String operator){
        return quartzBiz.triggerJob(jobGroup, jobName, operator);
    }

    @Override
    public ScheduleJob getJobByName(String jobGroup, String jobName){
        return quartzBiz.getJobByName(jobGroup, jobName);
    }

    @Override
    public PageResult<List<ScheduleJob>> listPage(Map<String, Object> paramMap, PageParam pageParam){
        return quartzBiz.listPage(paramMap, pageParam);
    }


}
