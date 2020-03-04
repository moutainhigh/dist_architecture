package com.xpay.service.message.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.message.entity.MailReceiver;
import com.xpay.facade.message.service.MessageManageFacade;
import com.xpay.service.message.biz.email.MailReceiveBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class MessageManageFacadeImpl implements MessageManageFacade {
    @Autowired
    MailReceiveBiz mailReceiveBiz;

    public boolean addMailReceiver(MailReceiver mailReceiver){
        return mailReceiveBiz.addMailReceiver(mailReceiver);
    }

    public boolean editMailReceiver(Long id, String from, String to, String remark){
        return mailReceiveBiz.editMailReceiver(id, from, to, remark);
    }

    public boolean deleteMailReceiver(Long id, String operator){
        return mailReceiveBiz.deleteMailReceiver(id, operator);
    }

    public PageResult<List<MailReceiver>> listPage(Map<String, Object> paramMap, PageParam pageParam){
        return mailReceiveBiz.listPage(paramMap, pageParam);
    }
}
