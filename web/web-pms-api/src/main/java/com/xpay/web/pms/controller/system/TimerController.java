package com.xpay.web.pms.controller.system;

import com.xpay.common.statics.annotations.Permission;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.statics.result.RestResult;
import com.xpay.common.util.utils.BeanUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.timer.entity.Instance;
import com.xpay.facade.timer.entity.OpLog;
import com.xpay.facade.timer.entity.ScheduleJob;
import com.xpay.facade.timer.service.QuartzAdminFacade;
import com.xpay.facade.timer.service.QuartzFacade;
import com.xpay.facade.user.pms.entity.PmsOperator;
import com.xpay.web.pms.annotation.CurrentUser;
import com.xpay.web.pms.controller.BaseController;
import com.xpay.web.pms.vo.system.ScheduleJobQueryVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.11.22
 * Time: 14:23
 * Description: 定时任务管理
 */
@RequestMapping("timer")
@RestController
public class TimerController extends BaseController {

    @Reference
    private QuartzFacade quartzFacade;

    @Reference
    private QuartzAdminFacade quartzAdminFacade;

    /**
     * 查询定时任务
     *
     * @return
     */
    @Permission("timer:schedule:view")
    @RequestMapping("listScheduleJob")
    public RestResult<PageResult<List<ScheduleJob>>> listScheduleJob(@RequestParam int pageCurrent, @RequestParam int pageSize, @RequestBody ScheduleJobQueryVO queryVO) {
        Map<String, Object> paramMap = BeanUtil.toMap(queryVO);
        PageResult<List<ScheduleJob>> pageResult = quartzFacade.listPage(paramMap, PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }


    /**
     * 查看定时任务详情
     *
     * @return
     */
    @Permission("timer:schedule:view")
    @RequestMapping("getScheduleJobInfo")
    public RestResult<ScheduleJob> getScheduleJobInfo(@RequestParam String jobGroup, @RequestParam String jobName) {
        if (StringUtil.isEmpty(jobGroup) || StringUtil.isEmpty(jobName)) {
            return RestResult.error("参数有误");
        }
        ScheduleJob scheduleJob = quartzFacade.getJobByName(jobGroup, jobName);
        if (scheduleJob == null) {
            return RestResult.error("定时任务不存在");
        } else {
            return RestResult.success(scheduleJob);
        }
    }


    @Permission("timer:schedule:add")
    @RequestMapping("addScheduleJob")
    public RestResult<String> addScheduleJob(@RequestBody ScheduleJob scheduleJob, @CurrentUser PmsOperator operator) {

        if (scheduleJob.getJobType() == null) {
            return RestResult.error("任务类型不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobGroup())) {
            return RestResult.error("任务分组不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobName())) {
            return RestResult.error("任务名称不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobDescription())) {
            return RestResult.error("任务描述不能为空");
        } else if (scheduleJob.getStartTime() == null) {
            return RestResult.error("开始时间不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getDestination())) {
            return RestResult.error("消息目的地不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getParamJson()) || !JsonUtil.isJson(scheduleJob.getParamJson())) {
            return RestResult.error("任务参数不能为空，且必须为json格式");
        }

        if (scheduleJob.getJobType() == ScheduleJob.SIMPLE_JOB) {
            if (scheduleJob.getIntervals() == null || scheduleJob.getIntervals() <= 0) {
                return RestResult.error("任务间隔需大于0");
            } else if (scheduleJob.getIntervalUnit() == null || scheduleJob.getIntervalUnit() <= 0) {
                return RestResult.error("任务间隔单位需大于0");
            }
        } else if (scheduleJob.getJobType() == ScheduleJob.CRON_JOB) {
            if (StringUtil.isEmpty(scheduleJob.getCronExpression())) {
                return RestResult.error("cron表达式不能为空");
            }
        } else {
            return RestResult.error("未预期的任务类型");
        }

        ScheduleJob newJob = new ScheduleJob();
        newJob.setJobType(scheduleJob.getJobType());
        newJob.setJobGroup(scheduleJob.getJobGroup());
        newJob.setJobName(scheduleJob.getJobName());
        newJob.setDestination(scheduleJob.getDestination());
        newJob.setStartTime(scheduleJob.getStartTime());
        newJob.setEndTime(scheduleJob.getEndTime());
        newJob.setJobDescription(scheduleJob.getDestination());
        newJob.setParamJson(scheduleJob.getParamJson());
        newJob.setMqType(1);
        if (scheduleJob.getJobType() == ScheduleJob.SIMPLE_JOB) {
            newJob.setIntervals(scheduleJob.getIntervals());
            newJob.setIntervalUnit(scheduleJob.getIntervalUnit());
        } else if (scheduleJob.getJobType() == ScheduleJob.CRON_JOB) {
            newJob.setCronExpression(scheduleJob.getCronExpression());
        }
        quartzFacade.add(newJob, operator.getLoginName());

        return RestResult.success("添加定时任务成功");
    }


    /**
     * 删除定时任务
     *
     * @return
     */
    @Permission("timer:schedule:edit")
    @RequestMapping("deleteScheduleJob")
    public RestResult<String> deleteScheduleJob(@RequestParam String jobGroup, @RequestParam String jobName, @CurrentUser PmsOperator operator) {
        if (StringUtil.isEmpty(jobGroup) || StringUtil.isEmpty(jobName)) {
            return RestResult.error("参数有误");
        }
        if (quartzFacade.delete(jobGroup, jobName, operator.getLoginName())) {
            return RestResult.success("操作成功");
        } else {
            return RestResult.error("操作失败");
        }
    }


    /**
     * 修改定时任务
     *
     * @return
     */
    @Permission("timer:schedule:edit")
    @RequestMapping("editScheduleJob")
    public RestResult<String> editScheduleJob(@RequestBody ScheduleJob scheduleJob, @CurrentUser PmsOperator operator) {

        if (scheduleJob.getJobType() == null) {
            return RestResult.error("任务类型不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobGroup())) {
            return RestResult.error("任务分组不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobName())) {
            return RestResult.error("任务名称不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getJobDescription())) {
            return RestResult.error("任务描述不能为空");
        } else if (scheduleJob.getStartTime() == null) {
            return RestResult.error("开始时间不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getDestination())) {
            return RestResult.error("消息目的地不能为空");
        } else if (StringUtil.isEmpty(scheduleJob.getParamJson()) || !JsonUtil.isJson(scheduleJob.getParamJson())) {
            return RestResult.error("任务参数不能为空，且必须为json格式");
        }

        final ScheduleJob current = quartzFacade.getJobByName(scheduleJob.getJobGroup(), scheduleJob.getJobName());

        if (current.getJobType() == ScheduleJob.SIMPLE_JOB) {
            if (scheduleJob.getIntervals() == null || scheduleJob.getIntervals() <= 0) {
                return RestResult.error("任务间隔需大于0");
            } else if (scheduleJob.getIntervalUnit() == null || scheduleJob.getIntervalUnit() <= 0) {
                return RestResult.error("任务间隔单位需大于0");
            }
        } else if (current.getJobType() == ScheduleJob.CRON_JOB) {
            if (StringUtil.isEmpty(scheduleJob.getCronExpression())) {
                return RestResult.error("cron表达式不能为空");
            }
        } else {
            return RestResult.error("未预期的任务类型");
        }


        current.setDestination(scheduleJob.getDestination());
        current.setJobDescription(scheduleJob.getJobDescription());
        current.setParamJson(scheduleJob.getParamJson());
        if (current.getJobType() == ScheduleJob.SIMPLE_JOB) {
            current.setIntervals(scheduleJob.getIntervals());
            current.setIntervalUnit(scheduleJob.getIntervalUnit());
        } else if (current.getJobType() == ScheduleJob.CRON_JOB) {
            current.setCronExpression(scheduleJob.getCronExpression());
        }

        if (quartzFacade.rescheduleJob(scheduleJob, operator.getLoginName())) {
            return RestResult.success("更新成功");
        } else {
            return RestResult.error("更新失败");
        }
    }


    /**
     * 立即触发一次任务
     *
     * @return
     */
    @Permission("timer:schedule:trigger")
    @RequestMapping("triggerScheduleJob")
    public RestResult<String> triggerScheduleJob(@RequestParam String jobGroup, @RequestParam String jobName, @CurrentUser PmsOperator operator) {
        if (StringUtil.isEmpty(jobGroup) || StringUtil.isEmpty(jobName)) {
            return RestResult.error("参数有误");
        }

        try {
            if (quartzFacade.triggerJob(jobGroup, jobName, operator.getLoginName())) {
                return RestResult.success("触发成功");
            } else {
                return RestResult.error("触发失败");
            }
        } catch (BizException e) {
            return RestResult.error("触发失败，" + e.getErrMsg());
        }
    }

    @Permission("timer:schedule:trigger")
    @RequestMapping("notifyScheduleJob")
    public RestResult<String> notifyScheduleJob(@RequestParam String jobGroup, @RequestParam String jobName, @CurrentUser PmsOperator operator) {
        if (StringUtil.isEmpty(jobGroup)) {
            return RestResult.error("任务分组不能为空");
        } else if (StringUtil.isEmpty(jobName)) {
            return RestResult.error("任务名称不能为空");
        }

        boolean isSuccess = quartzFacade.sendJobNotify(jobGroup, jobName, operator.getLoginName());
        if (isSuccess) {
            return RestResult.success("通知成功");
        } else {
            return RestResult.error("通知失败");
        }
    }

    /**
     * 恢复被暂停的定时任务
     *
     * @return
     */
    @Permission("timer:schedule:edit")
    @RequestMapping("changeScheduleJobStatus")
    public RestResult<String> changeScheduleJobStatus(@RequestParam String jobGroup, @RequestParam String jobName, @RequestParam String newStatus, @CurrentUser PmsOperator operator) {
        if (StringUtil.isEmpty(jobGroup) || StringUtil.isEmpty(jobName)) {
            return RestResult.error("参数有误");
        }
        boolean b;
        if ("NORMAL".equals(newStatus)) {
            b = quartzFacade.resumeJob(jobGroup, jobName, operator.getLoginName());
        } else if ("PAUSED".equals(newStatus)) {
            b = quartzFacade.pauseJob(jobGroup, jobName, operator.getLoginName());
        } else {
            return RestResult.error("参数错误");
        }

        if (b) {
            return RestResult.success("操作成功");
        } else {
            return RestResult.error("操作失败");
        }
    }

    @Permission("timer:instance:view")
    @RequestMapping("listInstance")
    public RestResult<List<Instance>> listInstance() {
        PageResult<List<Instance>> pageResult = quartzAdminFacade.listInstancePage(new HashMap<>(), PageParam.newInstance(1, 100, "namespace asc"));
        return RestResult.success(pageResult.getData());
    }

    @Permission("timer:instance:edit")
    @RequestMapping("changeNamespaceStatus")
    public RestResult<Boolean> changeNamespaceStatus(String namespace, Integer namespaceStatus, @CurrentUser PmsOperator operator) {
        boolean isSuccess = false;
        if (Integer.valueOf(1).equals(namespaceStatus)) {
            isSuccess = quartzAdminFacade.pauseAllInstanceAsync(namespace, operator.getLoginName());
        } else if (Integer.valueOf(2).equals(namespaceStatus)) {
            isSuccess = quartzAdminFacade.resumeAllInstanceAsync(namespace, operator.getLoginName());
        }
        return RestResult.success(isSuccess);
    }

    @Permission("timer:opLog:view")
    @RequestMapping("listOpLog")
    public RestResult<PageResult<List<OpLog>>> listOpLog(String objKey, int pageCurrent, int pageSize) {
        Map<String, Object> param = new HashMap<>();
        param.put("objKey", objKey);
        PageResult<List<OpLog>> pageResult = quartzAdminFacade.listOpLogPage(param, PageParam.newInstance(pageCurrent, pageSize));
        return RestResult.success(pageResult);
    }
}
