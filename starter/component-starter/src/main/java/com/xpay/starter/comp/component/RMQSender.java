package com.xpay.starter.comp.component;

import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.rmq.MsgDtoHeaderEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * MQ消息发送器，目前使用RocketMQ
 */
public class RMQSender {
    private String charset = "UTF-8";
    Logger log = LoggerFactory.getLogger(this.getClass());
    private RocketMQTemplate rocketMQTemplate;

    public RMQSender(RocketMQTemplate rocketMQTemplate){
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 发送单个消息
     * @param msg
     * @return
     */
    public boolean sendOne(MsgDto msg) {
        Message message = buildMessage(msg);

        SendResult sendResult = rocketMQTemplate.syncSend(getDestination(msg.getTopic(), msg.getTags()), message);
        return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
    }

    /**
     * 发送单个消息
     * @param msg
     * @param onFail    消息发送失败之后的处理
     * @return
     */
    public void sendOne(MsgDto msg, Consumer<MsgDto> onFail) {
        Message message = buildMessage(msg);

        SendResult sendResult = rocketMQTemplate.syncSend(getDestination(msg.getTopic(), msg.getTags()), message);
        if(! SendStatus.SEND_OK.equals(sendResult.getSendStatus()) && onFail != null){
            msg.setSendFailMsg(sendResult.getSendStatus().name());
            onFail.accept(msg);
        }
    }

    /**
     * 发送单个消息且不等待响应结果，优点是快速高效，缺点是有可能丢失消息
     * @param msg
     */
    public void sendOneWay(MsgDto msg) {
        Message message = buildMessage(msg);
        rocketMQTemplate.sendOneWay(getDestination(msg.getTopic(), msg.getTags()), message);
    }

    /**
     * 异步发送单个消息，优点是异步化可提高并发能力和发送效率，缺点是不能保障消息不丢（尤其是在应用重启时）
     * @param msg
     * @param callback  消息发送成功或失败之后的回调函数，如果不需要处理回调则设置为null即可
     */
    public void sendOneAsync(MsgDto msg, Consumer<MsgDto> callback) {
        Message message = buildMessage(msg);
        rocketMQTemplate.asyncSend(getDestination(msg.getTopic(), msg.getTags()), message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                if(callback != null){
                    if(SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
                        callback.accept(msg);
                    }else{
                        onException(new Throwable(sendResult.getSendStatus().name()));
                    }
                }
            }

            @Override
            public void onException(Throwable e) {
                if(callback != null){
                    msg.setSendFailMsg(e.getMessage());
                    callback.accept(msg);
                }
            }
        });
    }

    /**
     * 发送批量消息，适合同一个业务事件有多个业务系统需要做不同业务处理的时候使用
     * 注意：4.5.2版本下Broker端使用DledgerCommitLog模式时还不支持批量消息，会报 [CODE: 13 MESSAGE_ILLEGAL] 的异常，在常规的Master-Slave下可以
     * @param destination   目的地，如果只有topic，则只传topic名称即可，如果还有tags，则拼接成 topic:tags 的形式
     * @param msgList
     * @return
     */
    public boolean sendBatch(String destination, List<? extends MsgDto> msgList){
        try {
            long now = 0;
            boolean isDebugEnabled = log.isDebugEnabled();
            if(isDebugEnabled){
                now = System.currentTimeMillis();
            }

            List<Message> sprMsgList = new ArrayList<>(msgList.size());
            for(MsgDto msg : msgList){
                Message message = buildMessage(msg);
                sprMsgList.add(message);
            }
            SendResult sendResult = rocketMQTemplate.syncSend(destination, sprMsgList, 3000L);
            if(isDebugEnabled){
                log.debug("sendBatch message cost: {} ms, msgId:{}", (System.currentTimeMillis()-now), sendResult.getMsgId());
            }
            return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
        } catch (Throwable e) {
            log.error("sendBatch failed. destination:{}, msgList:{} ", destination, JsonUtil.toString(msgList));
            throw new BizException("批量消息发送异常", e);
        }
    }

    /**
     * 发送批量消息，一批次的消息只能发送给同一个topic，但tags可以不一样
     * @see #sendBatch(String, List)
     * @param msgList
     * @return
     */
    public boolean sendBatch(List<? extends MsgDto> msgList){
        try {
            long now = 0;
            boolean isDebugEnabled = log.isDebugEnabled();
            if(isDebugEnabled){
                now = System.currentTimeMillis();
            }

            List<org.apache.rocketmq.common.message.Message> rmsgList = new ArrayList<>(msgList.size());
            for(MsgDto msg : msgList){
                Message<MsgDto> message = buildMessage(msg);
                String destination = getDestination(msg.getTopic(), msg.getTags());
                org.apache.rocketmq.common.message.Message rocketMsg = RocketMQUtil.convertToRocketMessage(
                        rocketMQTemplate.getMessageConverter(), charset, destination, message);
                rmsgList.add(rocketMsg);
            }

            SendResult sendResult = rocketMQTemplate.getProducer().send(rmsgList);
            if(isDebugEnabled){
                log.debug("sendBatch message cost: {} ms, msgId:{}", (System.currentTimeMillis()-now), sendResult.getMsgId());
            }
            return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
        } catch (Throwable e) {
            log.error("sendBatch failed. msgList:{} ", JsonUtil.toString(msgList));
            throw new BizException("批量消息发送异常", e);
        }
    }

    /**
     * 发送事务消息
     * @param msg
     * @return
     */
    public boolean sendTrans(MsgDto msg) {
        Message message = buildMessage(msg);
        SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(getDestination(msg.getTopic(), msg.getTags()), message, null);
        return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
    }

    public static String getDestination(String topic , String tags){
        return StringUtil.isEmpty(tags) ? topic : topic + ":" + tags;
    }

    private Message buildMessage(MsgDto msg){
        Map<String, String> header = msg.getHeader();
        //只保留MsgDtoHeaderEnum中指定的header
        if (msg.getHeader() != null) {
            Map<String, String> msgDtoHeader = header.entrySet().stream()
                    .filter(p -> Arrays.stream(MsgDtoHeaderEnum.values()).anyMatch(e -> e.getHeaderKey().equals(p.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            msg.setHeader(msgDtoHeader.isEmpty() ? null : msgDtoHeader);
        }
        MessageBuilder builder = MessageBuilder.withPayload(msg);

        //把消息头设置到MQ消息头里面去
        if(header != null && !header.isEmpty()){
            for(Map.Entry<String, String> entry : header.entrySet()){
                if(entry.getKey() == null){
                    continue;
                }

                builder.setHeader(entry.getKey(), entry.getValue());
            }
        }
        builder.setHeader(MessageConst.PROPERTY_KEYS, msg.getTrxNo());

        return builder.build();
    }
}
