package com.xpay.extend.account.listener;

import com.alibaba.fastjson.JSONObject;
import com.xpay.common.statics.constants.rmqdest.AccountMsgDest;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.extend.account.biz.mch.AccountMchCallbackScheduleBiz;
import com.xpay.extend.account.biz.mch.AccountMchProcessScheduleBiz;
import com.xpay.extend.account.biz.sub.AccountSubCallbackScheduleBiz;
import com.xpay.extend.account.biz.sub.AccountSubProcessScheduleBiz;
import com.xpay.extend.account.biz.transit.AccountTransitCallbackScheduleBiz;
import com.xpay.extend.account.biz.transit.AccountTransitProcessScheduleBiz;
import com.xpay.extend.account.helper.ScheduleTaskHelper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Author: Cmf
 * Date: 2019.12.30
 * Time: 19:49
 * Description:
 */
@Component
public class AccountScheduleListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScheduleTaskHelper scheduleTaskHelper;

    @Autowired
    private AccountMchProcessScheduleBiz accountMchProcessScheduleBiz;
    @Autowired
    private AccountMchCallbackScheduleBiz accountMchCallbackScheduleBiz;

    @Autowired
    private AccountSubProcessScheduleBiz accountSubProcessScheduleBiz;
    @Autowired
    private AccountSubCallbackScheduleBiz accountSubCallbackScheduleBiz;

    @Autowired
    private AccountTransitProcessScheduleBiz accountTransitProcessScheduleBiz;
    @Autowired
    private AccountTransitCallbackScheduleBiz accountTransitCallbackScheduleBiz;


    @Component
    @RocketMQMessageListener(topic = AccountMsgDest.TOPIC_ACCOUNT_MCH_PROCESS, selectorExpression = AccountMsgDest.TAG_ACCOUNT_MCH_SCHEDULE_TASK, consumerGroup = "accountMchScheduleTask")
    public class AccountMchScheduleTaskListener implements RocketMQListener<MsgDto> {
        @Override
        public void onMessage(MsgDto msgDto) {
            logger.info("平台商户账务处理，接收到定时任务的消息通知msgDto={}", JsonUtil.toString(msgDto));
            try {
                JSONObject json = JsonUtil.toBean(msgDto.getJsonParam(), JSONObject.class);
                String taskType = json.getString("taskType");
                if (Objects.equals(taskType, "accountMchProcessAsync")) {
                    scheduleTaskHelper.addScheduleTask(accountMchProcessScheduleBiz::doAccountMchProcessSchedule, "account_mch_process_async", 2 * 60 * 60);//2小时过期
                } else if (Objects.equals(taskType, "accountMchCallback")) {
                    scheduleTaskHelper.addScheduleTask(accountMchCallbackScheduleBiz::doAccountMchCallbackSchedule, "account_mch_result_callback", 2 * 60 * 60);//2小时过期
                } else {
                    logger.error("未知的taskType类型:{}", taskType);
                }
            } catch (Exception ex) {
                logger.error("平台商户账务处理，处理定时任务消息通知时出现异常:msgDto={}", JsonUtil.toString(msgDto), ex);
            }
        }
    }


    @Component
    @RocketMQMessageListener(topic = AccountMsgDest.TOPIC_ACCOUNT_SUB_PROCESS, selectorExpression = AccountMsgDest.TAG_ACCOUNT_SUB_SCHEDULE_TASK, consumerGroup = "accountSubScheduleTask")
    public class AccountSubScheduleTaskListener implements RocketMQListener<MsgDto> {
        @Override
        public void onMessage(MsgDto msgDto) {
            logger.info("子商户账务处理，接收到定时任务的消息通知");
            try {
                JSONObject json = JsonUtil.toBean(msgDto.getJsonParam(), JSONObject.class);
                String taskType = json.getString("taskType");
                if (Objects.equals(taskType, "accountSubProcessAsync")) {
                    scheduleTaskHelper.addScheduleTask(accountSubProcessScheduleBiz::doAccountProcessSchedule, "account_sub_process_async", 2 * 60 * 60); //2小时过期
                } else if (Objects.equals(taskType, "accountSubCallback")) {
                    scheduleTaskHelper.addScheduleTask(accountSubCallbackScheduleBiz::doAccountCallbackSchedule, "account_sub_result_callback", 2 * 60 * 60);  //2小时过期
                } else {
                    logger.error("未知的taskType类型:{}", taskType);
                }
            } catch (Exception ex) {
                logger.error("子商户账务处理，处理定时任务消息通知时出现异常:msgDto={}", JsonUtil.toString(msgDto), ex);
            }
        }
    }

    @Component
    @RocketMQMessageListener(topic = AccountMsgDest.TOPIC_ACCOUNT_TRANSIT_PROCESS, selectorExpression = AccountMsgDest.TAG_ACCOUNT_TRANSIT_SCHEDULE_TASK, consumerGroup = "accountTransitScheduleTask")
    public class AccountTransitScheduleTaskListener implements RocketMQListener<MsgDto> {
        @Override
        public void onMessage(MsgDto msgDto) {
            logger.info("在途账户账务处理，接收到定时任务的消息通知");
            try {
                JSONObject json = JsonUtil.toBean(msgDto.getJsonParam(), JSONObject.class);
                String taskType = json.getString("taskType");
                if (Objects.equals(taskType, "accountTransitProcessAsync")) {
                    scheduleTaskHelper.addScheduleTask(accountTransitProcessScheduleBiz::doAccountProcessSchedule, "account_transit_process_async", 2 * 60 * 60); //2小时过期
                } else if (Objects.equals(taskType, "accountTransitCallback")) {
                    scheduleTaskHelper.addScheduleTask(accountTransitCallbackScheduleBiz::doAccountCallbackSchedule, "account_transit_result_callback", 2 * 60 * 60);  //2小时过期
                } else {
                    logger.error("未知的taskType类型:{}", taskType);
                }
            } catch (Exception ex) {
                logger.error("在途账户账务处理，处理定时任务消息通知时出现异常:msgDto={}", JsonUtil.toString(msgDto), ex);
            }
        }
    }

}
