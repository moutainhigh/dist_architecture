package com.xpay.service.notify.facade;

import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.notify.entity.NotifyRecord;
import com.xpay.facade.notify.entity.NotifyResponse;
import com.xpay.facade.notify.service.NotifyManageFacade;
import com.xpay.service.notify.core.biz.NotifyRecordBiz;
import com.xpay.service.notify.core.biz.NotifyResponseBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Author: Cmf
 * Date: 2019.11.28
 * Time: 18:30
 * Description:
 */
@Service
public class NotifyManageFacadeImpl implements NotifyManageFacade {

    @Autowired
    private NotifyRecordBiz notifyRecordBiz;

    @Autowired
    private NotifyResponseBiz notifyResponseBiz;

    @Override
    public NotifyRecord getNotifyRecordById(long notifyRecordId) {
        return notifyRecordBiz.getNotifyRecordById(notifyRecordId);
    }

    @Override
    public PageResult<List<NotifyRecord>> listNotifyRecordPage(Map<String, Object> paramMap, PageParam pageParam) {
        return notifyRecordBiz.listNotifyRecordPage(paramMap, pageParam);
    }

    @Override
    public List<NotifyResponse> listNotifyResponseByNotifyIds(List<Long> notifyIds) {
        return notifyResponseBiz.listByNotifyIds(notifyIds);
    }
}
