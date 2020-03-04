package com.xpay.service.timer.dao;

import org.springframework.stereotype.Repository;
import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.timer.entity.ScheduleJob;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenyf on 2017/8/29.
 */
@Repository
public class ScheduleJobDao extends MyBatisDao<ScheduleJob, Long> {

    public ScheduleJob getByName(String jobGroup, String jobName) {
        Map<String, Object> param = new HashMap<>();
        param.put("jobGroup", jobGroup);
        param.put("jobName", jobName);
        return super.getOne(param);
    }
}
