package com.xpay.service.message.biz.email;

import com.xpay.common.statics.dto.message.EmailParamDto;
import com.xpay.common.statics.constants.rmqdest.MessageMsgDest;
import com.xpay.common.statics.enums.message.EmailFromEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.message.dto.EmailMsgDto;
import com.xpay.facade.message.entity.MailReceiver;
import com.xpay.service.message.biz.resolver.TemplateResolver;
import com.xpay.service.message.dao.MailReceiverDao;
import com.xpay.starter.comp.component.EmailSender;
import com.xpay.starter.comp.component.RMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class EmailBiz {
    private final static String TEMPLATE_FOLDER = "email" + File.separator;//邮件模版的路径：classpath:templates/email/
    @Autowired
    EmailSender emailSender;
    @Autowired
    TemplateResolver templateResolver;
    @Autowired
    RMQSender rmqSender;
    @Autowired
    MailReceiverDao mailReceiverDao;

    public boolean send(String groupKey, String subject, String content){
        StringBuilder to = new StringBuilder();
        String[] cc = new String[]{};
        EmailFromEnum from = splitFromMailReceiver(groupKey, to, cc);
        return send(from, to.toString(), cc, subject, content);
    }

    public boolean sendAsync(String groupKey, String subject, String content){
        StringBuilder to = new StringBuilder();
        String[] cc = new String[]{};
        EmailFromEnum from = splitFromMailReceiver(groupKey, to, cc);
        return sendAsync(from, to.toString(), cc, subject, content, false);
    }

    public boolean sendHtml(String groupKey, String subject, String content){
        StringBuilder to = new StringBuilder();
        String[] cc = new String[]{};
        EmailFromEnum from = splitFromMailReceiver(groupKey, to, cc);
        return sendHtml(from, to.toString(), cc, subject, content);
    }

    public boolean sendHtmlAsync(String groupKey, String subject, String content){
        StringBuilder to = new StringBuilder();
        String[] cc = new String[]{};
        EmailFromEnum from = splitFromMailReceiver(groupKey, to, cc);
        return sendAsync(from, to.toString(), cc, subject, content, true);
    }

    private EmailFromEnum splitFromMailReceiver(String groupKey, StringBuilder to, String[] cc){
        MailReceiver mailReceiver = mailReceiverDao.getByGroupKey(groupKey);
        if(mailReceiver == null){
            throw new BizException(BizException.BIZ_INVALIDATE, "groupKey="+"groupKey对应的收件人记录不存在");
        }
        EmailFromEnum from = EmailFromEnum.getEnum(mailReceiver.getFrom());
        List<String> toList = JsonUtil.toList(mailReceiver.getTo(), String.class);
        if(toList != null && !toList.isEmpty()){
            for(int i=0; i<toList.size(); i++){
                if(i==0){
                    to.append(toList.get(i));
                }else{
                    cc[i-1] = toList.get(i);
                }
            }
        }
        return from;
    }

    public boolean send(EmailFromEnum from, String to, String[] cc, String subject, String content){
        return emailSender.sendTextMail(from.getAccount(), to, cc, subject, content);
    }

    public boolean sendAsync(EmailFromEnum from, String to, String[] cc, String subject, String content, boolean isHtml){
        EmailMsgDto msgDto = new EmailMsgDto();

        msgDto.setTopic(MessageMsgDest.TOPIC_SEND_EMAIL_ASYNC);
        msgDto.setTags(MessageMsgDest.TAG_SEND_EMAIL_ASYNC);
        msgDto.setTrxNo(from.getAccount());
        msgDto.setFrom(from);
        msgDto.setTo(to);
        msgDto.setCc(cc);
        msgDto.setSubject(subject);
        msgDto.setContent(content);
        msgDto.setHtmlFormat(isHtml);
        return rmqSender.sendOne(msgDto);
    }

    public boolean sendHtml(EmailFromEnum from, String to, String[] cc, String subject, String content){
        return emailSender.sendHtmlMail(from.getAccount(), to, cc, subject, content);
    }

    public boolean send(EmailParamDto param){
        String content = templateResolver.resolve(TEMPLATE_FOLDER + param.getTpl(), param.getTplParam());

        if(param.getHtmlFormat()){
            return emailSender.sendHtmlMail(param.getFrom().getAccount(), param.getTo(), param.getCc(), param.getSubject(), content);
        }else{
            return emailSender.sendTextMail(param.getFrom().getAccount(), param.getTo(), param.getCc(), param.getSubject(), content);
        }
    }

    public boolean sendAsync(EmailParamDto param){
        String trxNo = param.getFrom().getAccount() + "_" + param.getTpl();

        EmailMsgDto msgDto = new EmailMsgDto();
        msgDto.setTopic(MessageMsgDest.TOPIC_SEND_EMAIL_ASYNC);
        msgDto.setTags(MessageMsgDest.TAG_SEND_EMAIL_ASYNC);
        msgDto.setTrxNo(trxNo);
        msgDto.setFrom(param.getFrom());
        msgDto.setTo(param.getTo());
        msgDto.setCc(param.getCc());
        msgDto.setSubject(param.getSubject());
        msgDto.setTpl(param.getTpl());
        msgDto.setTplParam(param.getTplParam());
        msgDto.setHtmlFormat(param.getHtmlFormat());
        return rmqSender.sendOne(msgDto);
    }

    public boolean send(EmailMsgDto msgDto){
        String content;
        if(StringUtil.isNotEmpty(msgDto.getTpl())){
            content = templateResolver.resolve(TEMPLATE_FOLDER + msgDto.getTpl(), msgDto.getTplParam());
        }else{
            content = msgDto.getContent();
        }

        if(msgDto.getHtmlFormat()){
            return emailSender.sendHtmlMail(msgDto.getFrom().getAccount(), msgDto.getTo(), msgDto.getCc(), msgDto.getSubject(), content);
        }else{
            return emailSender.sendTextMail(msgDto.getFrom().getAccount(), msgDto.getTo(), msgDto.getCc(), msgDto.getSubject(), content);
        }
    }
}
