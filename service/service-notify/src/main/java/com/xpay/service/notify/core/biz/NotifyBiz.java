package com.xpay.service.notify.core.biz;

import com.xpay.common.statics.constants.common.PublicStatus;
import com.xpay.common.statics.constants.rmqdest.MqNotifyType;
import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.rmq.MsgDtoHeaderEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.notify.entity.NotifyRecord;
import com.xpay.facade.notify.entity.NotifyResponse;
import com.xpay.starter.comp.component.RMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Date;

@Service
public class NotifyBiz {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RMQSender rmqSender;
    @Autowired
    private NotifyRecordBiz notifyRecordBiz;
    @Autowired
    private NotifyResponseBiz notifyResponseBiz;


    /**
     * 发送通知
     *
     * @param merchantNo   商户编号
     * @param mqNotifyType 通知类型
     * @param msgBody      通知消息VO
     * @param trxNo        业务流水号
     * @param otherTrxNo   二级，三级业务流水号
     * @param <T>          通知消息VO类型
     */
    public <T> void sendNotify(String merchantNo, MqNotifyType<T> mqNotifyType, T msgBody, String trxNo, @Nullable String... otherTrxNo) {
        final String msgBodyJson = JsonUtil.toString(msgBody);

        NotifyRecord notifyRecord = new NotifyRecord();
        try {
            notifyRecord.setCreateTime(new Date());
            notifyRecord.setLastNotifyTime(new Date());
            notifyRecord.setMsgTopic(mqNotifyType.getTopic());
            notifyRecord.setMsgTag(mqNotifyType.getTag());
            notifyRecord.setMsgEventType(mqNotifyType.getEventType());
            notifyRecord.setContent(msgBodyJson);
            notifyRecord.setStatus(PublicStatus.ACTIVE);

            notifyRecord.setMerchantNo(merchantNo);
            notifyRecord.setTrxNo(trxNo);
            notifyRecord.setSubTrxNo(otherTrxNo != null && otherTrxNo.length >= 1 ? otherTrxNo[0] : "");
            notifyRecord.setThirdTrxNo(otherTrxNo != null && otherTrxNo.length >= 2 ? otherTrxNo[1] : "");
            notifyRecordBiz.createNotifyRecord(notifyRecord);
        } catch (Exception ex) {
            logger.error("创建notifyRecord失败,merchantNo={},notifyType={},msgBody={},trxNo={}", merchantNo, JsonUtil.toString(mqNotifyType), JsonUtil.toString(msgBody), trxNo, ex);
            throw ex;
        }

        MsgDto msgDto = new MsgDto();
        msgDto.setTopic(mqNotifyType.getTopic());
        msgDto.setTags(mqNotifyType.getTag());
        msgDto.setEventType(mqNotifyType.getEventType());
        msgDto.setTrxNo(trxNo);
        msgDto.setJsonParam(msgBodyJson);
        msgDto.addHeader(MsgDtoHeaderEnum.NOTIFY_RECORD_ID.getHeaderKey(), notifyRecord.getId() + "");

        boolean msgSendOk;
        try {
            msgSendOk = rmqSender.sendOne(msgDto);
        } catch (Exception ex) {
            logger.error("发送RocketMQ消息失败,merchantNo={},notifyType={},msgBody={},trxNo={}", merchantNo, JsonUtil.toString(mqNotifyType), JsonUtil.toString(msgBody), trxNo, ex);
            msgSendOk = false;
        }

        if (!msgSendOk) {
            try {
                notifyRecordBiz.updateNotifyRecordStatus(notifyRecord.getId(), PublicStatus.INACTIVE);
            } catch (Exception ex) {
                logger.error("更新失败通知记录为失败状态时发生异常,merchantNo={},notifyType={},msgBody={},trxNo={}", merchantNo, JsonUtil.toString(mqNotifyType), JsonUtil.toString(msgBody), trxNo, ex);
            }
        }
    }

    /**
     * 通知记录响应
     *
     * @param notifyRecordId .
     * @param rspSign        .
     * @param rspStatus      {@link PublicStatus}
     * @param rspMsg         响应信息
     */
    public void response(long notifyRecordId, String rspSign, int rspStatus, String rspMsg) {
        logger.info("response:msgID:{},rspSign:{},rspStatus:{},rspMsg:{}", notifyRecordId, rspSign, rspStatus, rspMsg);
        NotifyResponse response = new NotifyResponse();
        response.setCreateTime(new Date());
        response.setNotifyId(notifyRecordId);
        response.setRspSign(rspSign);
        response.setRspTime(new Date());
        response.setRspStatus(rspStatus);
        response.setRspMsg(rspMsg);
        try {
            notifyResponseBiz.createOrUpdateResponse(response);
        } catch (Exception ex) {
            logger.error("创建notifyResponse失败,notifyRecordId={},rspStatus={},rspMsg={}", notifyRecordId, rspStatus, rspMsg, ex);
            throw new BizException(BizException.BIZ_INVALIDATE, "NotifyResponse保存数据库失败");
        }
    }

    /**
     * 重发消息
     *
     * @param notifyId 通知记录id
     */
    public boolean resend(long notifyId) {
        NotifyRecord record = notifyRecordBiz.getNotifyRecordById(notifyId);
        if (record == null) {
            logger.info("通知记录不存在,重发消息失败,notifyId={}", notifyId);
            return false;
        }

        MsgDto msgDto = new MsgDto();
        msgDto.setTopic(record.getMsgTopic());
        msgDto.setTags(record.getMsgTag());
        msgDto.setEventType(record.getMsgEventType());
        msgDto.setTrxNo(record.getTrxNo());
        msgDto.setJsonParam(record.getContent());
        msgDto.addHeader(MsgDtoHeaderEnum.NOTIFY_RECORD_ID.getHeaderKey(), notifyId + "");

        boolean msgSendOk = false;
        try {
            msgSendOk = rmqSender.sendOne(msgDto);
        } catch (Exception ex) {
            logger.error("发送RocketMQ消息失败,notifyId={}", notifyId);
        }

        if (msgSendOk) {
            record.setLastNotifyTime(new Date());
            record.setStatus(PublicStatus.ACTIVE);
            notifyRecordBiz.updateNotifyRecord(record);
        }

        return msgSendOk;
    }


}
