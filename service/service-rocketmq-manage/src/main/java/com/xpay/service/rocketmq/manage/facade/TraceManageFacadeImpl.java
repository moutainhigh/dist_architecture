package com.xpay.service.rocketmq.manage.facade;

import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.facade.rocketmq.manage.entity.TraceEntity;
import com.xpay.facade.rocketmq.manage.service.TraceManageFacade;
import com.xpay.service.rocketmq.manage.core.biz.MessageManageBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019.11.28
 * Time: 18:30
 * Description:
 */
@Service
public class TraceManageFacadeImpl implements TraceManageFacade {

    @Autowired
    private MessageManageBiz messageManageBiz;

    @Override
    public List<TraceEntity> listTraceEntityPage(Date timeBegin, Date timeEnd) throws BizException {
        return messageManageBiz.listTraceEntity(timeBegin.getTime(), timeEnd.getTime());
    }

    @Override
    public TraceEntity getTraceDetail(String msgId) throws BizException {
        return messageManageBiz.getTraceDetail(msgId);
    }

    @Override
    public MsgDto getMessageContent(String topic, String msgId) {
        return messageManageBiz.getMessageContent(topic, msgId);

    }

    @Override
    public List<TraceEntity> listTraceEntityByTrxNo(String trxNo) {
        return messageManageBiz.listTraceEntityByTrxNo(trxNo);
    }
}
