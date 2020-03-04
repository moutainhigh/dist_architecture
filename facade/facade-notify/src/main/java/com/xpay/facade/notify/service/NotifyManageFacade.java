package com.xpay.facade.notify.service;


import com.xpay.common.statics.result.PageParam;
import com.xpay.common.statics.result.PageResult;
import com.xpay.facade.notify.entity.NotifyRecord;
import com.xpay.facade.notify.entity.NotifyResponse;

import java.util.List;
import java.util.Map;

/**
 * 业务通知管理服务
 */
public interface NotifyManageFacade {

    NotifyRecord getNotifyRecordById(long notifyRecordId);

    PageResult<List<NotifyRecord>> listNotifyRecordPage(Map<String, Object> paramMap, PageParam pageParam);

    List<NotifyResponse> listNotifyResponseByNotifyIds(List<Long> notifyIds);
}
