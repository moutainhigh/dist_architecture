package com.xpay.service.notify.core.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.facade.notify.entity.NotifyUnique;
import org.springframework.stereotype.Repository;

/**
 * Author: Cmf
 * Date: 2019.11.28
 * Time: 20:07
 * Description: 唯一约束dao
 */
@Repository
public class NotifyUniqueDao extends MyBatisDao<NotifyUnique, Long> {
}
