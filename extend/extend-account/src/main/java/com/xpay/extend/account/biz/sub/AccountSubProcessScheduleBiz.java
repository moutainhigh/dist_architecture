package com.xpay.extend.account.biz.sub;

import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accountsub.service.AccountSubProcessFacade;
import com.xpay.facade.accountsub.service.AccountSubProcessManageFacade;
import com.xpay.starter.comp.component.TaskExecutorPool;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * Description:子商户批量异步账务处理定时任务
 */

@Component("accountSubProcessScheduleBiz")
public class AccountSubProcessScheduleBiz {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int NUMBER_PER_PAGE = 3000;
    private static final int EXECUTE_THREAD_NUM = 8;

    @Reference
    private AccountSubProcessManageFacade accountSubProcessManageFacade;

    @Reference
    private AccountSubProcessFacade accountSubProcessFacade;

    @Autowired
    private TaskExecutorPool scheduleTaskExecutePool;


    public void doAccountProcessSchedule() {
        logger.info("doAccountProcessSchedule");
        Date createTimeBegin = DateUtil.addDay(new Date(), -1);
        Date createTimeEnd = new Date();
        Long mindId = null;
        Long[] successAndTotalCount = new Long[]{0L, 0L};

        while (true) {
            List<Long> pendingAccountProcessIdList = accountSubProcessManageFacade.listPendingAccountProcessId(createTimeBegin, createTimeEnd, mindId, NUMBER_PER_PAGE);
            successAndTotalCount[1] += pendingAccountProcessIdList.size();
            logger.info("子商户待账务处理批量处理,createTimeBegin:{},createTimeEnd:{},minId:{},查询到:{}条待账务处理记录",
                    DateUtil.formatDateTime(createTimeBegin), DateUtil.formatDateTime(createTimeEnd), mindId, pendingAccountProcessIdList.size());

            int batchNum = Math.min(EXECUTE_THREAD_NUM, pendingAccountProcessIdList.size());
            int batchSize = pendingAccountProcessIdList.size() / batchNum + (pendingAccountProcessIdList.size() % batchNum == 0 ? 0 : 1);
            List<Future<Long>> futureList = new ArrayList<>(batchNum);
            //将查询到的待账务处理分成若干组,进行处理
            for (int i = 0; i < batchNum; i++) {
                List<Long> subList = pendingAccountProcessIdList.subList(i * batchSize, Math.min((i + 1) * batchSize, pendingAccountProcessIdList.size()));
                try {
                    futureList.add(scheduleTaskExecutePool.submit(new AsyncProcessTask(subList)));
                } catch (Exception ex) {
                    //如果提交任务失失败
                    logger.error("子商户待账务处理批量处理,提交任务到线程池失败", ex);
                }
            }

            Date futureExpireTime = DateUtil.addMinute(new Date(), 2 * 60);              //等待结果的过期时间=当前时间+2小时
            futureList.stream().filter(Objects::nonNull).forEach(f -> {
                try {
                    successAndTotalCount[0] += f.get(futureExpireTime.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    logger.error("子商户待账务处理批量处理,获取处理结果等待超时", ex);
                }
            });

            //如果查询到的数据小于分页数，则不再循环查询
            if (pendingAccountProcessIdList.size() < NUMBER_PER_PAGE) {
                break;
            } else {
                mindId = pendingAccountProcessIdList.get(pendingAccountProcessIdList.size() - 1);
            }
        }
        logger.info("子商户批量异步账务处理完成,成功处理:{}笔,共{}笔", successAndTotalCount[0], successAndTotalCount[1]);
    }


    private class AsyncProcessTask implements Callable<Long> {
        private Logger logger = LoggerFactory.getLogger(this.getClass());


        private List<Long> pendingAccountProcessIdList;

        public AsyncProcessTask(List<Long> pendingAccountProcessIdList) {
            this.pendingAccountProcessIdList = pendingAccountProcessIdList;
        }

        @Override
        public Long call() {
            Long[] successCount = new Long[]{0L};
            logger.info("AsyncProcessTask accountProcessIdList.size=" + pendingAccountProcessIdList.size() + " start");
            pendingAccountProcessIdList.forEach(pendingId -> {
                try {
                    if (accountSubProcessFacade.executeSyncForAsync(pendingId)) {
                        successCount[0] += 1;
                    }
                } catch (Exception ex) {
                    logger.error("执行账务处理过程中出现异常,accountProcessPendingId={}", pendingId, ex);
                }
            });
            logger.info("AsyncProcessTask accountProcessIdList.size=" + pendingAccountProcessIdList.size() + " end");
            return successCount[0];
        }
    }


}
