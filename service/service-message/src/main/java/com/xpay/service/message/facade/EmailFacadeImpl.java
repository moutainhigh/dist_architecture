package com.xpay.service.message.facade;

import com.xpay.common.statics.dto.message.EmailParamDto;
import com.xpay.common.statics.enums.message.EmailFromEnum;
import com.xpay.facade.message.service.EmailFacade;
import com.xpay.service.message.biz.email.EmailBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailFacadeImpl implements EmailFacade {
    @Autowired
    EmailBiz emailBiz;

    public boolean send(String groupKey, String subject, String content){
        return emailBiz.send(groupKey, subject, content);
    }

    public boolean sendAsync(String groupKey, String subject, String content){
        return emailBiz.sendAsync(groupKey, subject, content);
    }

    public boolean sendHtml(String groupKey, String subject, String content){
        return emailBiz.sendHtml(groupKey,subject, content);
    }

    public boolean sendHtmlAsync(String groupKey, String subject, String content){
        return emailBiz.sendHtmlAsync(groupKey, subject, content);
    }

    @Override
    public boolean send(EmailFromEnum from, String to, String[] cc, String subject, String content){
        return emailBiz.send(from, to, cc, subject, content);
    }

    @Override
    public boolean sendHtml(EmailFromEnum from, String to, String[] cc, String subject, String content){
        return emailBiz.sendHtml(from, to, cc, subject, content);
    }

    @Override
    public boolean sendAsync(EmailFromEnum from, String to, String[] cc, String subject, String content){
        return emailBiz.sendAsync(from, to, cc, subject, content, false);
    }

    @Override
    public boolean sendHtmlAsync(EmailFromEnum from, String to, String[] cc, String subject, String content){
        return emailBiz.sendAsync(from, to, cc, subject, content, true);
    }

    @Override
    public boolean send(EmailParamDto param){
        return emailBiz.send(param);
    }

    @Override
    public boolean sendAsync(EmailParamDto param){
        return emailBiz.sendAsync(param);
    }
}
