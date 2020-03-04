package com.xpay.service.message.biz.sms;

import com.xpay.common.statics.dto.message.SmsParamDto;
import com.xpay.common.statics.enums.message.SmsTplLocalEnum;
import com.xpay.common.statics.enums.message.SmsTplPlatEnum;
import com.xpay.common.statics.enums.message.SmsPlatformEnum;
import com.xpay.common.statics.enums.message.SmsTplTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.service.message.biz.resolver.TemplateResolver;
import com.xpay.service.message.biz.sms.senders.AliyunSmsSender;
import com.xpay.service.message.biz.sms.senders.MongateSmsSender;
import com.xpay.service.message.biz.sms.senders.YunxinSmsSender;
import com.xpay.service.message.biz.sms.senders.ZhizlSmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsBiz {
    @Autowired
    TemplateResolver templateResolver;
    @Autowired
    AliyunSmsSender aliyunSmsSender;
    @Autowired
    MongateSmsSender mongateSmsSender;
    @Autowired
    YunxinSmsSender yunxinSmsSender;
    @Autowired
    ZhizlSmsSender zhizlSmsSender;

    public SmsSendResp send(String phone, String msg, String bizKey){
        SmsSender smsSender = getSmsSender(SmsPlatformEnum.ALI_YUN);//默认使用阿里云发送
        return smsSender.send(phone, msg, bizKey);
    }

    public SmsSendResp send(SmsParamDto smsParam){
        if(SmsTplTypeEnum.PLATFORM.equals(smsParam.getTplType())){ //使用运营商平台模版
            String tplCode = SmsTplPlatEnum.getEnum(smsParam.getTplName()).getCode();
            SmsSender smsSender = getSmsSender(smsParam.getPlatform());
            return smsSender.send(smsParam.getPhone(), tplCode, smsParam.getTplParam(), smsParam.getBizKey());
        }else if(SmsTplTypeEnum.LOCAL.equals(smsParam.getTplType())){ //使用本地系统模版(freemarker实现)
            String tplCode = SmsTplLocalEnum.getEnum(smsParam.getTplName()).getCode();
            String msg = templateResolver.resolve(tplCode, smsParam.getTplParam());
            SmsSender smsSender = getSmsSender(smsParam.getPlatform());
            return smsSender.send(smsParam.getPhone(), msg, smsParam.getBizKey());
        }else{
            throw new BizException("未支持的短信模版类型 tplType：" + smsParam.getTplType());
        }
    }

    public SmsQueryResp query(SmsPlatformEnum platform, SmsQueryParam queryParam){
        SmsSender smsSender = getSmsSender(platform);
        return smsSender.getSingleSmsStatus(queryParam);
    }

    private SmsSender getSmsSender(SmsPlatformEnum smsPlatform){
        if(smsPlatform == null){
            throw new BizException("请指定短信运营商！");
        }

        switch (smsPlatform){
            case ALI_YUN:
                return aliyunSmsSender;
            case MON_GATE:
                return mongateSmsSender;
            case YUN_XIN:
                return yunxinSmsSender;
            case ZHI_ZL:
                return zhizlSmsSender;
            default:
                throw new BizException("未预期的短信运营商");
        }
    }
}
