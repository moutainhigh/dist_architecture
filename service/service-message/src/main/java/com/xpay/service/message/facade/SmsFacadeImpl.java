package com.xpay.service.message.facade;

import com.xpay.common.statics.dto.message.SmsParamDto;
import com.xpay.common.statics.enums.message.SmsPlatformEnum;
import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.facade.message.service.SmsFacade;
import com.xpay.service.message.biz.sms.SmsBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SmsFacadeImpl implements SmsFacade {
    @Autowired
    SmsBiz smsBiz;

    /**
     * 短信发送接口，系统会选择默认的运营商和通用的短信模版进行发送
     * @param phone        收信人手机号
     * @param msg          短信内容
     * @return
     */
    @Override
    public SmsSendResp send(String phone, String msg, String bizKey){
        return smsBiz.send(phone, msg, bizKey);
    }

    /**
     * 短信发送接口，可指定短信运营商、短信模版类型、短信模版名称、短信模版参数 等等的参数来发送
     * @param smsParam
     * @return
     */
    @Override
    public SmsSendResp send(SmsParamDto smsParam){
        return smsBiz.send(smsParam);
    }

    @Override
    public SmsQueryResp query(SmsPlatformEnum platform, SmsQueryParam queryParam){
        return smsBiz.query(platform, queryParam);
    }
}
