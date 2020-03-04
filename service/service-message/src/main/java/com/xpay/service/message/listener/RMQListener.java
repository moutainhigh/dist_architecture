package com.xpay.service.message.listener;

import com.xpay.common.statics.constants.rmqdest.MessageMsgDest;
import com.xpay.facade.message.dto.EmailMsgDto;
import com.xpay.service.message.biz.email.EmailBiz;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RMQListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    EmailBiz emailBiz;

    @Component
    @RocketMQMessageListener(topic = MessageMsgDest.TOPIC_SEND_EMAIL_ASYNC, selectorExpression = MessageMsgDest.TOPIC_SEND_EMAIL_ASYNC, consumeThreadMax = 8, consumerGroup = "emailAsyncConsume")
    public class emailAsyncConsume implements RocketMQListener<EmailMsgDto> {
        public void onMessage(EmailMsgDto msgDto) {
            logger.info("trxNo={} to={} 接收到异步邮件消息", msgDto.getTrxNo(), msgDto.getTo());
            emailBiz.send(msgDto);
        }
    }
}
