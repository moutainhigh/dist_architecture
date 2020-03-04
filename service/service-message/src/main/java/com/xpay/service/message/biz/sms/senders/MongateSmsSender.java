package com.xpay.service.message.biz.sms.senders;

import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.service.message.biz.sms.SmsSender;
import com.xpay.service.message.config.MongateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 梦网短信发送
 */
@Service
public class MongateSmsSender implements SmsSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String successStatus = "0";

    @Autowired
    private MongateConfig mongateConfig;

    @Override
    public String prefixName(String tplCode){
        return null;
    }

    @Override
    public SmsSendResp send(String phone, String msg, String bizKey) {
        return null;
    }

    @Override
    public SmsSendResp send(String phone, String tplCode, Map<String, Object> tplParam, String bizKey){
        return null;
    }

    /**
     * 获取短信发送状态
     */
    @Override
    public SmsQueryResp getSingleSmsStatus(SmsQueryParam queryParam){
        return null;
    }
}
