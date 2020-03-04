package com.xpay.service.message.biz.email;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.message.entity.MailReceiver;
import com.xpay.service.message.dao.MailReceiverDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MailReceiveBiz {
    @Autowired
    MailReceiverDao mailReceiverDao;

    public boolean addMailReceiver(MailReceiver mailReceiver){
        MailReceiver receiver = mailReceiverDao.getByGroupKey(mailReceiver.getGroupKey());
        if(receiver != null){
            throw new BizException(BizException.BIZ_INVALIDATE, "GroupKey="+mailReceiver.getGroupKey()+"对应的记录已存在");
        }
        mailReceiverDao.insert(mailReceiver);
        return mailReceiver.getId() != null;
    }

    public boolean editMailReceiver(Long id, String from, String to, String remark){
        MailReceiver receiver = mailReceiverDao.getById(id);
        if(receiver == null){
            throw new BizException(BizException.BIZ_INVALIDATE, "记录不存在");
        }
        if(StringUtil.isNotEmpty(from)){
            receiver.setFrom(from);
        }
        if(StringUtil.isNotEmpty(to)){
            receiver.setTo(to);
        }
        if(StringUtil.isNotEmpty(remark)){
            receiver.setRemark(remark);
        }
        mailReceiverDao.updateIfNotNull(receiver);
        return true;
    }

    public boolean deleteMailReceiver(Long id, String operator){
        MailReceiver receiver = mailReceiverDao.getById(id);
        if(receiver == null){
            throw new BizException(BizException.BIZ_INVALIDATE, "记录不存在");
        }
        mailReceiverDao.deleteById(id);
        return true;
    }

    public PageResult<List<MailReceiver>> listPage(Map<String, Object> paramMap, PageParam pageParam){
        return mailReceiverDao.listPage(paramMap, pageParam);
    }
}
