package com.xpay.facade.message.service;

import com.xpay.common.statics.dto.message.EmailParamDto;
import com.xpay.common.statics.enums.message.EmailFromEnum;

/**
 * 邮件发送接口
 */
public interface EmailFacade {

    public boolean send(String groupKey, String subject, String content);

    public boolean sendAsync(String groupKey, String subject, String content);

    public boolean sendHtml(String groupKey, String subject, String content);

    public boolean sendHtmlAsync(String groupKey, String subject, String content);

    /**
     * 同步发送邮件
     * @param from      发件人
     * @param to        收件人
     * @param cc        抄送，若没有则传null
     * @param subject   邮件主题
     * @param content   邮件内容
     * @return
     */
    public boolean send(EmailFromEnum from, String to, String[] cc, String subject, String content);

    /**
     * 异步发送邮件
     * @param from      发件人
     * @param to        收件人
     * @param cc        抄送，若没有则传null
     * @param subject   邮件主题
     * @param content   邮件内容
     * @return
     */
    public boolean sendAsync(EmailFromEnum from, String to, String[] cc, String subject, String content);

    /**
     * 同步发送HTML格式的邮件
     * @param from      发件人
     * @param to        收件人
     * @param cc        抄送，若没有则传null
     * @param subject   邮件主题
     * @param content   邮件内容
     * @return
     */
    public boolean sendHtml(EmailFromEnum from, String to, String[] cc, String subject, String content);

    /**
     * 异步发送HTML格式的邮件
     * @param from      发件人
     * @param to        收件人
     * @param cc        抄送，若没有则传null
     * @param subject   邮件主题
     * @param content   邮件内容
     * @return
     */
    public boolean sendHtmlAsync(EmailFromEnum from, String to, String[] cc, String subject, String content);

    /**
     * 使用freemarker模版引擎同步发送邮件
     * @param param     邮件参数
     * @return
     */
    public boolean send(EmailParamDto param);

    /**
     * 使用freemarker模版引擎异步发送邮件
     * @param param     邮件参数
     * @return
     */
    public boolean sendAsync(EmailParamDto param);
}
