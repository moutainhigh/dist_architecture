package com.xpay.service.timer.test;

import com.xpay.common.statics.dto.migrate.MigrateParamDto;
import com.xpay.common.statics.enums.common.TimeUnitEnum;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.constants.rmqdest.MigrateMsgDest;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.timer.entity.ScheduleJob;
import com.xpay.facade.timer.service.QuartzAdminFacade;
import com.xpay.facade.timer.service.QuartzFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

public class TimerTest extends BaseTestCase {
    @Reference
    QuartzFacade quartzFacade;
    @Reference
    QuartzAdminFacade quartzAdminFacade;

    @Ignore
    @Test
    public void testTimer() throws Exception {
        String jobGroup = "migrateGroup";
        String jobName = "migrateJob";
        ScheduleJob job = ScheduleJob.newSimpleTask(jobGroup, jobName, MigrateMsgDest.TOPIC_DATA_MIGRATION);
        job.setToRepeatForeverTask(new Date(), 10, TimeUnitEnum.SECOND.getValue());

        quartzFacade.delete(jobGroup, jobName, "tester");

        MigrateParamDto paramDto = new MigrateParamDto();
        paramDto.setMigratorName("ACCOUNT_MCH_MIGRATION");
        paramDto.setMigrateTypeArr(new String[]{DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_DETAIL.name(),
                DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_PENDING.name(), DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_RESULT.name(),
                DataMigrateTypeEnum.ACCOUNT_MCH_COMMON_UNIQUE.name()});
        job.setParamJson(JsonUtil.toString(paramDto));
        quartzFacade.add(job, "tester");

        Thread.sleep(50000);
    }

    @Ignore
    @Test
    public void testInstance(){
        String namespace = "dev";
        quartzAdminFacade.pauseAllInstanceAsync(namespace, "tester");
        try{
            Thread.sleep(5000);
        }catch(Exception e){e.printStackTrace();}


        quartzAdminFacade.resumeAllInstanceAsync(namespace, "tester");
        try{
            Thread.sleep(50000);
        }catch(Exception e){e.printStackTrace();}
    }
}
