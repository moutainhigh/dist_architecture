package com.xpay.facade.message.dto;

import com.xpay.common.statics.dto.rmq.MsgDto;
import com.xpay.common.statics.enums.message.EmailFromEnum;

public class EmailMsgDto extends MsgDto {
    /**
     * 发件人
     */
    private EmailFromEnum from;
    /**
     * 收件人，必填
     */
    private String to;
    /**
     * 抄送人
     */
    private String[] cc;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 是否发送html格式的邮件
     */
    private boolean htmlFormat;

    /**
     * 邮件模版
     */
    private String tpl;
    /**
     * 模版内的参数
     */
    private Object tplParam;


    /**
     * 邮件内容（当使用tpl模版时，此值为null）
     */
    private String content;

    public EmailFromEnum getFrom() {
        return from;
    }

    public void setFrom(EmailFromEnum from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean getHtmlFormat() {
        return htmlFormat;
    }

    public void setHtmlFormat(boolean htmlFormat) {
        this.htmlFormat = htmlFormat;
    }

    public String getTpl() {
        return tpl;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

    public Object getTplParam() {
        return tplParam;
    }

    public void setTplParam(Object tplParam) {
        this.tplParam = tplParam;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
