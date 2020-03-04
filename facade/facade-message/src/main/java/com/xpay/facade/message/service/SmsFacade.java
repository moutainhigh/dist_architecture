package com.xpay.facade.message.service;

import com.xpay.common.statics.dto.message.SmsParamDto;
import com.xpay.common.statics.enums.message.SmsPlatformEnum;
import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;

/**
 * 短信发送接口
 */
public interface SmsFacade {

    /**
     * 短信发送接口，系统会选择默认的运营商和通用的短信模版进行发送
     * @param phone        收信人手机号
     * @param msg          短信内容
     * @return
     */
    public SmsSendResp send(String phone, String msg, String bizKey);

    /**
     * 短信发送接口，可指定短信运营商、短信模版类型、短信模版名称、短信模版参数 等等的参数来发送
     * @param smsParam
     * @return
     */
    public SmsSendResp send(SmsParamDto smsParam);

    /**
     * 查询结果
     * @param platform
     * @param queryParam
     * @return
     */
    public SmsQueryResp query(SmsPlatformEnum platform, SmsQueryParam queryParam);
}
