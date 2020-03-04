package com.xpay.service.message.biz.sms.senders;

import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.service.message.biz.sms.SmsSender;
import com.xpay.service.message.config.ZhiZLConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 智众联短信发送
 */
@Service
public class ZhizlSmsSender implements SmsSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ZhiZLConfig zhiZLConfig;

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

    @Override
    public SmsQueryResp getSingleSmsStatus(SmsQueryParam queryParam){
        return null;
    }
}
