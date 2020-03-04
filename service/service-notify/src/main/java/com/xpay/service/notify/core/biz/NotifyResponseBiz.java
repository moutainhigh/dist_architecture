package com.xpay.service.notify.core.biz;

import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.common.util.utils.MD5Util;
import com.xpay.facade.notify.entity.NotifyResponse;
import com.xpay.facade.notify.entity.NotifyUnique;
import com.xpay.service.notify.core.dao.NotifyResponseDao;
import com.xpay.service.notify.core.dao.NotifyUniqueDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotifyResponseBiz {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    NotifyResponseDao notifyResponseDao;

    @Autowired
    NotifyUniqueDao notifyUniqueDao;

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateResponse(NotifyResponse notifyResponse) throws BizException {
        try {
            NotifyUnique unique = new NotifyUnique();
            String strUnique = "NotifyResponse_" +
                    notifyResponse.getNotifyId() + "_" +
                    notifyResponse.getRspSign();

            unique.setUniqueKey(MD5Util.getMD5Hex(strUnique));
            unique.setCreateDate(new Date());
            notifyUniqueDao.insert(unique);
            notifyResponseDao.insert(notifyResponse);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("UNIQUE_KEY")) {
                logger.info("通知响应唯一约束已存在，直接进行更新,notifyResponse={}", JsonUtil.toString(notifyResponse));
                Optional.ofNullable(notifyResponseDao.getByNotifyIdAndRspSign(notifyResponse.getNotifyId(), notifyResponse.getRspSign()))
                        .ifPresent(response -> {
                            final List<NotifyResponse.RspHistory> rspHistories = response.getRspHistoriesAsList();
                            rspHistories.add(new NotifyResponse.RspHistory(response.getRspTime(), response.getRspStatus(), response.getRspMsg()));
                            response.setRspTime(new Date());
                            response.setRspMsg(notifyResponse.getRspMsg());
                            response.setRspStatus(notifyResponse.getRspStatus());
                            response.setRspHistories(JsonUtil.toString(rspHistories));
                            notifyResponseDao.update(response);
                        });
            } else {
                logger.error("保存response失败", e);
                throw new BizException(BizException.BIZ_INVALIDATE, "NotifyResponse保存数据库失败");
            }
        }
    }

    public List<NotifyResponse> listByNotifyId(long notifyId) {
        return notifyResponseDao.listByNotifyIds(Collections.singletonList(notifyId));
    }


    public List<NotifyResponse> listByNotifyIds(List<Long> notifyIds) {
        return notifyResponseDao.listByNotifyIds(notifyIds);
    }
}
