/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accountsub.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.account.AccountProcessPendingStageEnum;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accountsub.entity.AccountSubProcessPending;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountSubProcessPendingDao extends MyBatisDao<AccountSubProcessPending, Long> {

    /**
     * 查询未处理的待账务处理记录id
     *
     * @param createTimeBegin .
     * @param createTimeEnd .
     * @param minId           循环查询时使用上一次的最大id作为本次查询的minId，第一次循环时，传null
     * @param number          查询的记录数
     * @return .
     */
    public List<Long> listPendingAccountProcessId(Date createTimeBegin, Date createTimeEnd, Long minId, int number) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("createTimeEnd", createTimeEnd);
        paramMap.put("minId", minId);
        paramMap.put("number", number);
        paramMap.put("processStage", AccountProcessPendingStageEnum.PENDING.getValue());

        return listBy("listPendingAccountProcessId", paramMap);
    }

    public boolean updatePendingStatus(String accountProcessNo, AccountProcessPendingStageEnum stageNew, AccountProcessPendingStageEnum stageOld) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("accountProcessNo", accountProcessNo);
        paramMap.put("stageNew", stageNew.getValue());
        paramMap.put("stageOld", stageOld.getValue());
        return update("updatePendingStatus", paramMap) > 0;
    }


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
        paramMap.put("processStage", AccountProcessPendingStageEnum.FINISHED.getValue());
        paramMap.put("migrateNumPerTime", migrationDto.getMigrateNumPerTime());
        return getSqlSession().selectList(fillSqlId("listIdsForMigration"), paramMap);
    }

    public int migratePendingByIds(List<Long> idList) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("idList", idList);
        return insert("migratePendingByIds", param);
    }

    public int deletePendingByIds(List<Long> detailIdList) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("idList", detailIdList);
        return deleteBy("migratePendingByIds", param);
    }
    //endregion

}
