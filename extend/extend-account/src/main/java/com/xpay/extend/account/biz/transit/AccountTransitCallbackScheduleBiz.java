package com.xpay.extend.account.biz.transit;

import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accounttransit.service.AccountTransitProcessManageFacade;
import com.xpay.starter.comp.component.TaskExecutorPool;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Author: Cmf
 * Date: 2020.1.8
 * Time: 15:04
 * Description:在途账户账务处理结果批量回调定时任务
 */
@Service
public class AccountTransitCallbackScheduleBiz {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int NUMBER_PER_PAGE = 3000;
    private static final int EXECUTE_THREAD_NUM = 8;

    @Reference
    private AccountTransitProcessManageFacade accountTransitProcessManageFacade;

    @Autowired
    private TaskExecutorPool scheduleTaskExecutePool;


    public void doAccountCallbackSchedule() {
        logger.info("doAccountCallbackSchedule");
        Date createTimeBegin = DateUtil.addDay(new Date(), -1);
        Long mindId = null;
        Long[] successAndTotalCount = new Long[]{0L, 0L};

        while (true) {
            List<Long> processResultIdList = accountTransitProcessManageFacade.listNeedCallBackResultId(createTimeBegin, mindId, NUMBER_PER_PAGE);
            successAndTotalCount[1] += processResultIdList.size();
            logger.info("在途账户账务处理结果回调通知批量处理,createTimeBegin:{},minId:{},查询到:{}条账务处理结果记录",
                    DateUtil.formatDateTime(createTimeBegin), mindId, processResultIdList.size());

            int batchNum = Math.min(EXECUTE_THREAD_NUM, processResultIdList.size());
            int batchSize = processResultIdList.size() / batchNum + (processResultIdList.size() % batchNum == 0 ? 0 : 1);
            List<Future<Long>> futureList = new ArrayList<>(batchNum);
            //将查询到的账务处理结果分成若干组,进行处理
            for (int i = 0; i < batchNum; i++) {
                List<Long> subList = processResultIdList.subList(i * batchSize, Math.min((i + 1) * batchSize, processResultIdList.size()));
                try {
                    futureList.add(scheduleTaskExecutePool.submit(new AccountTransitCallbackScheduleBiz.CallbackTask(subList)));
                } catch (Exception ex) {
                    //如果提交任务失失败
                    logger.error("在途账户账务处理结果回调通知批量处理,提交任务到线程池失败", ex);
                }
            }
            Date futureExpireTime = DateUtil.addMinute(new Date(), 2 * 60);              //等待结果的过期时间=当前时间+2小时
            futureList.stream().filter(Objects::nonNull).forEach(f -> {
                try {
                    successAndTotalCount[0] += f.get(futureExpireTime.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    logger.error("在途账户账务处理结果回调通知批量处理,获取通知结果等待超时", ex);
                }
            });

            //如果查询到的数据小于分页数，则不再循环查询
            if (processResultIdList.size() < NUMBER_PER_PAGE) {
                break;
            } else {
                mindId = processResultIdList.get(processResultIdList.size() - 1);
            }
        }
        logger.info("在途账户账务处理结果回调通知批量处理,成功处理:{}笔,共{}笔", successAndTotalCount[0], successAndTotalCount[1]);
    }


    private class CallbackTask implements Callable<Long> {
        private Logger logger = LoggerFactory.getLogger(this.getClass());

        private List<Long> processResultIdList;

        public CallbackTask(List<Long> processResultIdList) {
            this.processResultIdList = processResultIdList;
        }

        @Override
        public Long call() {
            Long[] successCount = new Long[]{0L};
            logger.info("CallbackTask processResultIdList.size=" + processResultIdList.size() + " start");
            processResultIdList.forEach(resultId -> {
                try {
                    if (accountTransitProcessManageFacade.sendProcessResultCallbackMsg(resultId, false, false)) {
                        successCount[0] += 1;
                    }
                } catch (Exception ex) {
                    logger.error("执行账务处理回调出现异常,processResultId={}", resultId, ex);
                }
            });
            logger.info("CallbackTask processResultIdList.size=" + processResultIdList.size() + " end");
            return successCount[0];
        }
    }
}
