package com.xpay.service.notify;

import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.constants.rmqdest.NotifyTestDest;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.rmq.MsgDtoHeaderEnum;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.notify.service.NotifyFacade;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("rmqListener1")
public class RMQListener {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NotifyFacade notifyFacade;

    @Component("notifyTestConsume")
    @RocketMQMessageListener(topic = NotifyTestDest.TOPIC_NOTIFY_TEST, selectorExpression = NotifyTestDest.TAG_NOTIFY_TEST, consumeThreadMax = 8, consumerGroup = "testConsume0")
    public class notifyTestConsume implements RocketMQListener<MsgDto> {

        @PostConstruct
        private void init() {
            System.out.println("init");
        }

        public void onMessage(MsgDto msgDto) {
            try {
                logger.info("接收到异步消息,msg={}", JsonUtil.toStringPretty(msgDto));
                notifyFacade.response(Long.parseLong(msgDto.getHeader().get(MsgDtoHeaderEnum.NOTIFY_RECORD_ID.getHeaderKey())), "成本计费", PublicStatus.ACTIVE, "成功");
            } catch (Exception e) {
                logger.info("", e);
            }
        }
    }


    @Component("notifyTestConsume1")
    @RocketMQMessageListener(topic = NotifyTestDest.TOPIC_NOTIFY_TEST, selectorExpression = NotifyTestDest.TAG_NOTIFY_TEST, consumeThreadMax = 8, consumerGroup = "testConsume1")
    public class notifyTestConsume1 implements RocketMQListener<MsgDto> {

        @PostConstruct
        private void init() {
            System.out.println("init");
        }

        public void onMessage(MsgDto msgDto) {
            try {
                logger.info("接收到异步消息,msg={}", JsonUtil.toStringPretty(msgDto));
                notifyFacade.response(Long.parseLong(msgDto.getHeader().get(MsgDtoHeaderEnum.NOTIFY_RECORD_ID.getHeaderKey())), "清结算", PublicStatus.ACTIVE, "成功");
            } catch (Exception e) {
                logger.info("", e);
            }
        }
    }


}
