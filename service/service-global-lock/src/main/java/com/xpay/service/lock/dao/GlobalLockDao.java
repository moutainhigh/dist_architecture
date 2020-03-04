package com.xpay.service.lock.dao;


import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.lock.entity.GlobalLock;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenyf
 */
@Repository
public class GlobalLockDao extends MyBatisDao<GlobalLock, Long> {

    public GlobalLock getByResourceId(String resourceId) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("resourceId", resourceId);
        return getOne("getByResourceId", param);
    }


}
