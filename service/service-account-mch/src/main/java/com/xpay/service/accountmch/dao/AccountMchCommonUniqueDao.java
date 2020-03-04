/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accountmch.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accountmch.entity.AccountMchCommonUnique;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountMchCommonUniqueDao extends MyBatisDao<AccountMchCommonUnique, Long>{
    //region 数据迁移相关

    public long getMigrationMaxId(Date migrationEndDate) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("createTimeBegin", DateUtil.getDayStart(migrationEndDate));
        paramMap.put("createTimeEnd", DateUtil.getDayEnd(migrationEndDate));
        return Optional.ofNullable((Long) getOne("getMigrationMaxId", paramMap)).orElse(0L);
    }


    public List<Long> listIdsForMigration(DataMigrationDto migrationDto) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("maxId", migrationDto.getMaxId());
        paramMap.put("migrateEndDate", DateUtil.getDayEnd(migrationDto.getMigrateEndDate()));
        paramMap.put("migrateNumPerTime", migrationDto.getMigrateNumPerTime());
        return getSqlSession().selectList(fillSqlId("listIdsForMigration"), paramMap);
    }

    public int deleteUniqueByIds(List<Long> detailIdList) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("idList", detailIdList);
        return deleteBy("deleteUniqueByIds", param);
    }
    //endregion

}
