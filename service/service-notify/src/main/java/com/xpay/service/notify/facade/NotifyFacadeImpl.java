package com.xpay.service.notify.facade;

import com.xpay.common.statics.constants.rmqdest.MqNotifyType;
import com.xpay.facade.notify.service.NotifyFacade;
import com.xpay.service.notify.core.biz.NotifyBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Cmf
 * Date: 2019.11.28
 * Time: 18:30
 * Description:业务消息通知服务
 */
@Service
public class NotifyFacadeImpl implements NotifyFacade {
    @Autowired
    private NotifyBiz notifyBiz;

    @Override
    public <T> void sendNotify(String merchantNo, MqNotifyType<T> mqNotifyType, T msgBody, String trxNo, String... otherTrxNo) {
        notifyBiz.sendNotify(merchantNo, mqNotifyType, msgBody, trxNo, otherTrxNo);
    }

    @Override
    public void response(long notifyId, String rspSign, int rspStatus, String rspMsg) {
        notifyBiz.response(notifyId, rspSign, rspStatus, rspMsg);
    }

    @Override
    public boolean resend(long notifyId) {
        return notifyBiz.resend(notifyId);
    }
}
