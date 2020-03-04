package com.xpay.service.message.biz.sms;

import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;

import java.util.Map;

/**
 * 短信发送接口
 */
public interface SmsSender {
    public String DEFAULT_PREFIX_NAME = "汇聚支付";

    /**
     * 短信前缀名称
     * @return
     */
    public String prefixName(String tplCode);

    /**
     * 发送短信，使用默认的通用模版或者不使用模版直接发送信息
     * @param phone     手机号
     * @param msg       要发送的短信内容
     * @param bizKey    业务流水号，可选
     */
    public SmsSendResp send(String phone, String msg, String bizKey);

    /**
     * 发送短信，须指定模版编码和模版参数
     * @param phone         手机号
     * @param tplCode       平台模版编码
     * @param tplParam      平台模版参数
     * @param bizKey        业务流水号，可选
     * @return
     */
    public SmsSendResp send(String phone, String tplCode, Map<String, Object> tplParam, String bizKey);

    /**
     * 获取单条短信发送状态
     */
    public SmsQueryResp getSingleSmsStatus(SmsQueryParam queryParam);
}
