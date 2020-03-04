package com.xpay.facade.message.service;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.message.entity.MailReceiver;

import java.util.List;
import java.util.Map;

public interface MessageManageFacade {
    public boolean addMailReceiver(MailReceiver mailReceiver);

    public boolean editMailReceiver(Long id, String from, String to, String remark);

    public boolean deleteMailReceiver(Long id, String operator);

    public PageResult<List<MailReceiver>> listPage(Map<String, Object> paramMap, PageParam pageParam);
}
