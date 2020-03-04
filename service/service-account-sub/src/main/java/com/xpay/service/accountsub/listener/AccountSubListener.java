package com.xpay.service.accountsub.listener;

import com.alibaba.fastjson.JSONObject;
import com.xpay.common.statics.constants.rmqdest.AccountMsgDest;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.service.accountsub.biz.AccountSubProcessHandler;
import com.xpay.service.accountsub.biz.AccountSubProcessResultBiz;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Cmf
 * Date: 2019.12.30
 * Time: 19:49
 * Description:
 */
@Component
public class AccountSubListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountSubProcessResultBiz accountSubProcessResultBiz;

    @Autowired
    private AccountSubProcessHandler accountSubProcessHandler;

    /***
     * 通知进行账务处理结果回调的MQ监听器
     */
    @Component
    @RocketMQMessageListener(topic = AccountMsgDest.TOPIC_ACCOUNT_SUB_PROCESS, selectorExpression = AccountMsgDest.TAG_ACCOUNT_SUB_CALLBACK, consumerGroup = "accountSubCallback")
    public class AccountSubCallbackListener implements RocketMQListener<MsgDto> {
        public void onMessage(MsgDto msgDto) {
            logger.info("子商户账务处理，接收进行账务处理结果回调的通知:{}", JsonUtil.toString(msgDto));
            long processResultId;
            try {
                JSONObject json = JsonUtil.toBean(msgDto.getJsonParam(), JSONObject.class);
                processResultId = json.getLong("processResultId");
            } catch (Exception ex) {
                logger.error("子商户通知进行账务处理结果回调，解析MQ消息出现异常,msgDto={}", JsonUtil.toString(msgDto));
                return;
            }
            try {
                accountSubProcessResultBiz.sendProcessResultCallbackMsg(processResultId, false, false);
            } catch (Exception ex) {
                logger.error("执行账务处理结果回调过程中出现异常,msgDto={}", JsonUtil.toString(msgDto), ex);
                throw ex;
            }
        }
    }


    /***
     * 通知执行异步账务处理的MQ监听器
     */
    @Component
    @RocketMQMessageListener(topic = AccountMsgDest.TOPIC_ACCOUNT_SUB_PROCESS, selectorExpression = AccountMsgDest.TAG_ACCOUNT_SUB_PROCESS_ASYNC, consumerGroup = "accountSubAsyncProcess")
    public class AccountSubAsyncProcessListener implements RocketMQListener<MsgDto> {
        public void onMessage(MsgDto msgDto) {
            logger.info("子商户账务处理，接收到进行异步账务处理的通知:{}", JsonUtil.toString(msgDto));
            long processPendingId;
            try {
                JSONObject json = JsonUtil.toBean(msgDto.getJsonParam(), JSONObject.class);
                processPendingId = json.getLong("processPendingId");
            } catch (Exception ex) {
                logger.error("子商户通知进行异步账务处理，解析MQ消息出现异常,msgDto={}", JsonUtil.toString(msgDto));
                return;
            }
            try {
                accountSubProcessHandler.executeSyncForAsync(processPendingId);
            } catch (Exception ex) {
                logger.error("异步账务处理执行过程中出现异常,msgDto={}", JsonUtil.toString(msgDto), ex);
                throw ex;
            }

        }
    }


}
