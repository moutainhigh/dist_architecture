package com.xpay.service.rocketmq.manage.core.biz;

import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.rocketmq.manage.entity.TraceConsumeInfo;
import com.xpay.facade.rocketmq.manage.entity.TraceEntity;
import com.xpay.facade.rocketmq.manage.entity.TracePubInfo;
import com.xpay.service.rocketmq.manage.util.MsgTraceDecodeUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.trace.TraceContext;
import org.apache.rocketmq.client.trace.TraceType;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Cmf
 * Date: 2019.12.20
 * Time: 17:43
 * Description:
 */
@Service
public class MessageManageBiz {
    private Logger logger = LoggerFactory.getLogger(MessageManageBiz.class);
    @Autowired
    RocketMQProperties rocketMQProperties;

    @Autowired
    private DefaultMQAdminExt defaultMQAdminExt;
    @Autowired
    private DefaultMQPullConsumer defaultMQPullConsumer;


    /**
     * 查询指定时间段内的trace信息
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return .
     * @throws BizException .
     */
    public List<TraceEntity> listTraceEntity(long begin, long end) throws BizException {
        try {
            List<MessageExt> messageExtList = listMessageByTime(MixAll.RMQ_SYS_TRACE_TOPIC, begin, end);
            List<TraceContext> traceContextList = transToTraceContextList(messageExtList);
            return transToTraceEntity(traceContextList);
        } catch (Exception ex) {
            logger.error("查询MessageTrace过程中出现异常", ex);
            throw new BizException(BizException.BIZ_INVALIDATE, "查出Message Trace过程中出现异常");
        }
    }

    /**
     * 根据消息key查询其trace信息列表
     *
     * @param trxNo 消息key
     * @return .
     */
    public List<TraceEntity> listTraceEntityByTrxNo(String trxNo) {
        try {
            List<MessageExt> messageExtList = defaultMQAdminExt.queryMessage(MixAll.RMQ_SYS_TRACE_TOPIC, trxNo, 2000, 0, System.currentTimeMillis()).getMessageList();
            List<TraceContext> traceContextList = transToTraceContextList(messageExtList).stream().filter(ctx -> Arrays.asList(ctx.getTraceBeans().get(0).getKeys().split(" ")).contains(trxNo)).collect(Collectors.toList());
            List<TraceEntity> traceEntityList = transToTraceEntity(traceContextList);
            return traceEntityList;
        } catch (Exception ex) {
            logger.error("查询MessageTrace过程中出现异常", ex);
            throw new BizException(BizException.BIZ_INVALIDATE, "查出Message Trace过程中出现异常");
        }
    }

    /**
     * 根据消息id查询其trace信息
     *
     * @param msgId 消息id
     * @return .
     */
    public TraceEntity getTraceDetail(String msgId) {
        try {
            List<MessageExt> messageExtList = defaultMQAdminExt.queryMessage(MixAll.RMQ_SYS_TRACE_TOPIC, msgId, 2000, 0, System.currentTimeMillis()).getMessageList();
            List<TraceContext> traceContextList = transToTraceContextList(messageExtList).stream().filter(ctx -> ctx.getTraceBeans().get(0).getMsgId().equals(msgId)).collect(Collectors.toList());
            List<TraceEntity> traceEntityList = transToTraceEntity(traceContextList);
            if (traceEntityList.size() == 1) {
                return traceEntityList.get(0);
            } else {
                throw new RuntimeException("查询数据有误");
            }
        } catch (Exception ex) {
            logger.error("查询MessageTrace过程中出现异常", ex);
            throw new BizException(BizException.BIZ_INVALIDATE, "查出Message Trace过程中出现异常");
        }
    }


    /**
     * 查询原始消息内容
     *
     * @param topic topic
     * @param msgId 消息id
     * @return .
     */
    public MsgDto getMessageContent(String topic, String msgId) {
        String msg = null;
        try {
            MessageExt messageExt = defaultMQAdminExt.viewMessage(topic, msgId);
            msg = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            return JsonUtil.toBean(msg, MsgDto.class);
        } catch (Exception ex) {
            logger.error("查询MessageTrace过程中出现异常,msg={}", msg, ex);
            throw new BizException(BizException.BIZ_INVALIDATE, "查出Message Trace过程中出现异常");
        }
    }


    /**
     * 查询指定topic中在某一时间段内的所有消息
     *
     * @param topicName topic名称
     * @param start     开始时间
     * @param end       结束时间
     * @return .
     * @throws BizException .
     */
    private List<MessageExt> listMessageByTime(String topicName, long start, long end) throws BizException {
        List<MessageExt> messageViewList = new ArrayList<>();
        try {
            Set<MessageQueue> mqs = defaultMQPullConsumer.fetchSubscribeMessageQueues(topicName);
            MQ_LOOP:
            for (MessageQueue mq : mqs) {
                long minOffset = defaultMQPullConsumer.searchOffset(mq, start);
                long maxOffset = defaultMQPullConsumer.searchOffset(mq, end);
                for (long offset = minOffset; offset <= maxOffset; ) {
                    try {
                        if (messageViewList.size() > 2000) {
                            break MQ_LOOP;
                        }
                        PullResult pullResult = defaultMQPullConsumer.pull(mq, "*", offset, 32);
                        offset = pullResult.getNextBeginOffset();
                        switch (pullResult.getPullStatus()) {
                            case FOUND:
                                List<MessageExt> messageListByQuery = pullResult.getMsgFoundList().stream()
                                        .filter(p -> p.getStoreTimestamp() >= start && p.getStoreTimestamp() <= end)
                                        .collect(Collectors.toList());
                                messageViewList.addAll(messageListByQuery);
                                break;
                            case NO_MATCHED_MSG:
                            case NO_NEW_MSG:
                            case OFFSET_ILLEGAL:
                                break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            messageViewList.sort((o1, o2) -> {
                if (o1.getStoreTimestamp() - o2.getStoreTimestamp() == 0) {
                    return 0;
                }
                return (o1.getStoreTimestamp() > o2.getStoreTimestamp()) ? -1 : 1;
            });
            return messageViewList;
        } catch (Exception e) {
            logger.error("查询消息出现异常,topicName={},start={},end={}", topicName, start, end, e);
            throw new BizException(BizException.UNEXPECT_ERROR, "查出消息过程中出现异常");
        }
    }


    private List<TraceEntity> transToTraceEntity(List<TraceContext> traceContextList) {

        List<TraceEntity> traceEntityList = traceContextList.stream().filter(ctx -> ctx.getTraceType() == TraceType.Pub).map(ctx -> {
            TracePubInfo tracePubInfo = new TracePubInfo();
            tracePubInfo.setMsgId(ctx.getTraceBeans().get(0).getMsgId());
            tracePubInfo.setPubTime(new Date(ctx.getTimeStamp()));
            tracePubInfo.setTopic(ctx.getTraceBeans().get(0).getTopic());
            tracePubInfo.setTags(ctx.getTraceBeans().get(0).getTags());
            tracePubInfo.setKeys(ctx.getTraceBeans().get(0).getKeys());
            tracePubInfo.setPubGroupName(ctx.getGroupName());

            TraceEntity traceEntity = new TraceEntity();
            traceEntity.setMsgId(tracePubInfo.getMsgId());
            traceEntity.setKey(tracePubInfo.getKeys());
            traceEntity.setPub(tracePubInfo);
            return traceEntity;
        }).collect(Collectors.toList());

        //对所有的pubInfo,使用msgId进行分组
        Map<String, List<TraceEntity>> msgIdGroupEntity = traceEntityList.stream().collect(Collectors.groupingBy(TraceEntity::getMsgId));

        //对所有的subInfo，使用requestId进行分组
        Map<String, List<TraceContext>> requestIdGroupedSub = traceContextList.stream()
                .filter(ctx -> ctx.getTraceType() == TraceType.SubBefore || ctx.getTraceType() == TraceType.SubAfter)
                .collect(Collectors.groupingBy(TraceContext::getRequestId));

        requestIdGroupedSub.forEach((requestId, subList) -> {
            TraceConsumeInfo traceConsumeInfo = new TraceConsumeInfo();
            traceConsumeInfo.setConsumeTraceId(requestId);
            subList.forEach(sub -> {
                if (sub.getTraceType() == TraceType.SubBefore) {
                    traceConsumeInfo.setMsgId(sub.getTraceBeans().get(0).getMsgId());
                    traceConsumeInfo.setConsumeTime(new Date(sub.getTimeStamp()));
                    traceConsumeInfo.setConsumeGroupName(sub.getGroupName());
                } else {
                    traceConsumeInfo.setConsumeStatus(sub.isSuccess());
                }
            });
            if (traceConsumeInfo.getMsgId() != null && msgIdGroupEntity.containsKey(traceConsumeInfo.getMsgId())) {
                if (!msgIdGroupEntity.get(traceConsumeInfo.getMsgId()).get(0).getGroupConsumeInfos().containsKey(traceConsumeInfo.getConsumeGroupName())) {
                    msgIdGroupEntity.get(traceConsumeInfo.getMsgId()).get(0).getGroupConsumeInfos().put(traceConsumeInfo.getConsumeGroupName(), new ArrayList<>());
                }
                msgIdGroupEntity.get(traceConsumeInfo.getMsgId()).get(0).getGroupConsumeInfos().get(traceConsumeInfo.getConsumeGroupName()).add(traceConsumeInfo);
            }
        });
        traceEntityList.forEach(p -> {
            p.getGroupConsumeInfos().values().forEach(consumeInfos -> {
                consumeInfos.sort(Comparator.comparing(TraceConsumeInfo::getConsumeTime).reversed());
            });
        });
        return traceEntityList;
    }


    private List<TraceContext> transToTraceContextList(List<MessageExt> extList) {
        List<TraceContext> traceContexts = new ArrayList<>();
        extList.forEach(p -> {
            List<TraceContext> rx = MsgTraceDecodeUtil.decoderFromTraceDataString(new String(p.getBody(), StandardCharsets.UTF_8));
            traceContexts.addAll(rx);
        });
        return traceContexts;
    }


}
