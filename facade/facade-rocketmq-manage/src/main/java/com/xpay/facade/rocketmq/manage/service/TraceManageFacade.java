package com.xpay.facade.rocketmq.manage.service;


import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.facade.rocketmq.manage.entity.TraceEntity;

import java.util.Date;
import java.util.List;

/**
 * ROCKETMQ消息跟踪管理服务
 */
public interface TraceManageFacade {
    List<TraceEntity> listTraceEntityPage(Date timeBegin, Date timeEnd) throws BizException;

    TraceEntity getTraceDetail(String msgId) throws BizException;

    MsgDto getMessageContent(String topic, String msgId);

    List<TraceEntity> listTraceEntityByTrxNo(String trxNo);
}
