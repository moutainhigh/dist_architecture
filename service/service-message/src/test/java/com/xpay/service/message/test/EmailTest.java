package com.xpay.service.message.test;

import com.xpay.common.statics.dto.message.EmailParamDto;
import com.xpay.common.statics.enums.message.EmailFromEnum;
import com.xpay.facade.message.service.EmailFacade;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class EmailTest extends BaseTestCase {
    private String to = "chenyufeng@joinpay.com";
    private String[] cc = new String[]{"caimengfei@joinpay.com"};

    @Reference
    EmailFacade emailFacade;

    @Ignore
    @Test
    public void sendTextEmail(){
        String content = "<h1 style=\"color:red;\">一封测试邮件</h1>";
        emailFacade.send(EmailFromEnum.ALIYUN_JOINPAY, to, null, "测试service-message", content);
    }

    @Ignore
    @Test
    public void sendHtmlEmail(){
        String content = "<h1 style=\"color:red;\">一封测试邮件</h1>";
        emailFacade.sendHtml(EmailFromEnum.ALIYUN_JOINPAY, to, null, "测试service-message", content);
    }

    @Ignore
    @Test
    public void sendFtlEmail(){
        String content = "<h1 style='color:red;'>一封测试邮件</h1>";

        Map<String, Object> tplParam = new HashMap<>();
        tplParam.put("content", content);

        EmailParamDto mailParam = new EmailParamDto();
        mailParam.setFrom(EmailFromEnum.ALIYUN_JOINPAY);
        mailParam.setTo(to);
        mailParam.setCc(cc);
        mailParam.setSubject("测试service-message");
        mailParam.setTpl("hello.ftl");
        mailParam.setTplParam(tplParam);
        mailParam.setHtmlFormat(true);

        emailFacade.send(mailParam);
    }

    @Ignore
    @Test
    public void sendFtlEmailAsync(){
        String content = "<h1 style='color:red;'>一封测试邮件</h1>";

        Map<String, Object> tplParam = new HashMap<>();
        tplParam.put("content", content);

        EmailParamDto mailParam = new EmailParamDto();
        mailParam.setFrom(EmailFromEnum.ALIYUN_JOINPAY);
        mailParam.setTo(to);
//        mailParam.setCc(cc);
        mailParam.setSubject("测试service-message");
        mailParam.setTpl("hello.ftl");
        mailParam.setTplParam(tplParam);
        mailParam.setHtmlFormat(true);

        emailFacade.sendAsync(mailParam);
    }
}
