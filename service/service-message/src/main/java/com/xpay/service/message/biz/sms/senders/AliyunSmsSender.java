package com.xpay.service.message.biz.sms.senders;

import com.alibaba.alicloud.sms.ISmsService;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.xpay.common.statics.enums.message.SmsTplPlatEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.AliSmsQueryParam;
import com.xpay.facade.message.params.SmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.service.message.biz.sms.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信平台发送器
 */
@Service
public class AliyunSmsSender implements SmsSender {
    private Logger logger = LoggerFactory.getLogger(AliyunSmsSender.class);
    public final static String SUCCESS_SEND_STATUS = "OK";
    public final static long SUCCESS_RECEIVE_STATUS = 3;

    @Autowired
    private ISmsService smsService;

    /**
     * 短信前缀名称
     * @return
     */
    @Override
    public String prefixName(String tplCode){
        return DEFAULT_PREFIX_NAME;
    }

    /**
     * 发送短信
     * @param phone
     * @param msg
     */
    @Override
    public SmsSendResp send(String phone, String msg, String bizKey){
        Map<String, Object> tplParam = new HashMap<>();
        tplParam.put("msg", msg);
        return send(phone, SmsTplPlatEnum.ALIYUN_COMMON.getCode(), tplParam, bizKey);
    }

    public SmsSendResp send(String phone, String tplCode, Map<String, Object> tplParam, String bizKey){
        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phone);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(prefixName(tplCode));
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(tplCode);
        request.setTemplateParam(JsonUtil.toString(tplParam));
        if(StringUtil.isNotEmpty(bizKey)){
            request.setOutId(bizKey);
        }

        try {
            SmsSendResp resp = new SmsSendResp();
            SendSmsResponse sendResp = smsService.sendSmsRequest(request);
            resp.setCode(sendResp.getCode());
            resp.setMessage(sendResp.getMessage());
            resp.setSerialNo(sendResp.getBizId());
            resp.setIsSuccess(SUCCESS_SEND_STATUS.equalsIgnoreCase(sendResp.getCode()));

            return resp;
        } catch (ClientException e) {
            logger.error("短信发送异常 phone={}", phone, e);
            throw new BizException(e);
        }
    }

    /**
     * 获取单条短信发送状态
     */
    @Override
    public SmsQueryResp getSingleSmsStatus(SmsQueryParam queryParam){
        AliSmsQueryParam param = (AliSmsQueryParam) queryParam;
        // 组装请求对象
        QuerySendDetailsRequest queryRequest = new QuerySendDetailsRequest();
        // 必填-号码
        queryRequest.setPhoneNumber(param.getPhone());
        // 必填-短信发送的日期 支持30天内记录查询（可查其中一天的发送数据），格式yyyyMMdd
        queryRequest.setSendDate(param.getSendDate());
        // 必填-页大小
        queryRequest.setPageSize(1L);
        // 必填-当前页码从1开始计数
        queryRequest.setCurrentPage(1L);
        queryRequest.setBizId(param.getSerialNo());

        try {
            QuerySendDetailsResponse response = smsService.querySendDetails(queryRequest);
            SmsQueryResp resp = new SmsQueryResp();
            resp.setCode(response.getCode());
            resp.setMessage(response.getMessage());
            if(SUCCESS_SEND_STATUS.equals(response.getCode()) && response.getSmsSendDetailDTOs() != null && response.getSmsSendDetailDTOs().size() > 0){
                QuerySendDetailsResponse.SmsSendDetailDTO detailDTO = response.getSmsSendDetailDTOs().get(0);
                resp.setBizKey(detailDTO.getOutId());
                resp.setSendDate(detailDTO.getSendDate());
                resp.setReceiveDate(detailDTO.getReceiveDate());
                resp.setSendStatus(String.valueOf(detailDTO.getSendStatus()));
                resp.setIsSuccess(SUCCESS_RECEIVE_STATUS == detailDTO.getSendStatus());
            }
            return resp;
        } catch (ClientException e) {
            logger.error("查询短信发送结果异常 serialNo={} phone={} s", param.getSerialNo(), param.getPhone(), e);
            throw new BizException(e);
        }
    }
}
