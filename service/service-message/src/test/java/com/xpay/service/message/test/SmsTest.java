package com.xpay.service.message.test;

import com.xpay.common.statics.dto.message.SmsParamDto;
import com.xpay.common.statics.enums.message.SmsPlatformEnum;
import com.xpay.common.statics.enums.message.SmsTplPlatEnum;
import com.xpay.common.statics.enums.message.SmsTplTypeEnum;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.RandomUtil;
import com.xpay.facade.message.dto.SmsSendResp;
import com.xpay.facade.message.params.AliSmsQueryParam;
import com.xpay.facade.message.params.SmsQueryResp;
import com.xpay.facade.message.service.SmsFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsTest extends BaseTestCase {
    @Reference
    private SmsFacade smsFacade;
    private String phone = "15919621523";

    @Test
    public void sendDefaultTest(){
        smsFacade.send(phone, "发送一条测试短信", "11111111");
    }

    @Test
    public void sendAliyunTest(){
        SmsParamDto smsParam = new SmsParamDto();
        smsParam.setPhone(phone);
        smsParam.setPlatform(SmsPlatformEnum.ALI_YUN);
        smsParam.setTplType(SmsTplTypeEnum.PLATFORM);
        smsParam.setTplName(SmsTplPlatEnum.ALIYUN_REGISTER.name());
        smsParam.setBizKey("2222222222222222");

        Map<String, Object> tplParam = new HashMap<>();
        tplParam.put("code", RandomUtil.getDigitStr(4));
        smsParam.setTplParam(tplParam);
        SmsSendResp sendResp = smsFacade.send(smsParam);

        try{
            Thread.sleep(5000);//休眠5秒，等待短信被接收
        }catch(Exception e){
            e.printStackTrace();
        }

        AliSmsQueryParam queryParam = new AliSmsQueryParam();
        queryParam.setPhone(phone);
        queryParam.setSendDate(DateUtil.formatCompactDate(new Date()));
        queryParam.setSerialNo(sendResp.getSerialNo());
        SmsQueryResp queryResp = smsFacade.query(SmsPlatformEnum.ALI_YUN, queryParam);
        System.out.println("queryResp = " + JsonUtil.toString(queryResp));
    }
}
