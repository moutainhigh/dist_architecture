package com.xpay.service.notify.core.dao;

import com.google.common.collect.Maps;
import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.StringUtil;
import com.xpay.facade.notify.entity.NotifyResponse;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class NotifyResponseDao extends MyBatisDao<NotifyResponse, Long> {

    /**
     * 根据通知记录id和响应方，查找通知响应
     *
     * @param notifyId 通知id
     * @param rspSign  响应方
     * @return 响应记录
     */
    public NotifyResponse getByNotifyIdAndRspSign(long notifyId, String rspSign) {
        if (StringUtil.isEmpty(rspSign)) {
            throw new BizException(BizException.PARAM_INVALIDATE, "rspSign不能为空");
        }
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("notifyId", notifyId);
        paramMap.put("rspSign", rspSign);
        return getOne(paramMap);
    }


    public List<NotifyResponse> listByNotifyIds(List<Long> notifyIds) {
        if (notifyIds == null || notifyIds.size() == 0) {
            return Collections.emptyList();
        } else {
            return listBy("listByNotifyIds", Collections.singletonMap("notifyIds", notifyIds));
        }
    }


}
