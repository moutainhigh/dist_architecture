package com.xpay.service.message.biz.sms.senders;

import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.service.message.biz.sms.SmsSender;
import com.xpay.service.message.config.YunXinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 云信短信发送
 *
 * @author linguangsheng
 * @date 2019/5/6
 */
@Service
public class YunxinSmsSender implements SmsSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static String successStatus = "DELIVRD";

    @Autowired
    private YunXinConfig yunXinConfig;

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
     * 获取通道名
     */
    @Override
    public SmsQueryResp getSingleSmsStatus(SmsQueryParam queryParam){
        return null;
    }
}
