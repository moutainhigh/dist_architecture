/*
 * Powered By [joinPay.com]
 */
package com.xpay.service.accountmch.dao;

import com.xpay.common.service.dao.MyBatisDao;
import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.account.AccountProcessResultAuditStageEnum;
import com.xpay.common.statics.enums.account.AccountProcessResultCallbackStageEnum;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.facade.accountmch.entity.AccountMchProcessResult;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountMchProcessResultDao extends MyBatisDao<AccountMchProcessResult, Long>{

/*    public List<Long> listAccountProcessResultId(Map<String, Object> paramMap) {
        return listBy("listAccountProcessResultId", paramMap);
    }*/

    /**
     * 如果更新成功（当且仅当将发送状态由“待发送”更新为“已发送”时，返回true）
     *
     * @param ID .
     * @return
     */
    public boolean updateAccountProcessResultToSentById(long ID) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ID", ID);
        paramMap.put("newStage", AccountProcessResultCallbackStageEnum.SENT.getValue());
        paramMap.put("oldStage", AccountProcessResultCallbackStageEnum.PENDING.getValue());
        return update("updateProcessResultCallbackStage", paramMap) > 0;
    }

    /**
     * 查询需要进行回调通知的账务处理结果记录id
     *
     * @param createTimeBegin .
     * @param minId           循环查询时使用上一次的最大id作为本次查询的minId，第一次循环时，传null
     * @param number          查询的记录数
     * @return .
     */
    public List<Long> listNeedCallBackResultId(Date createTimeBegin, Long minId, int number) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("createTimeBegin", createTimeBegin);
        paramMap.put("minId", minId);
        paramMap.put("number", number);
        paramMap.put("auditStageList", Arrays.asList(AccountProcessResultAuditStageEnum.AUDIT_FINISHED.getValue(), AccountProcessResultAuditStageEnum.AUDIT_NONE.getValue()));
        paramMap.put("callbackStage", AccountProcessResultCallbackStageEnum.PENDING.getValue());

        return listBy("listNeedCallBackResultId", paramMap);
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
        paramMap.put("callbackStages", Arrays.asList(AccountProcessResultCallbackStageEnum.SENT.getValue()
                , AccountProcessResultCallbackStageEnum.NONE_SEND.getValue()));
        paramMap.put("migrateNumPerTime", migrationDto.getMigrateNumPerTime());
        return getSqlSession().selectList(fillSqlId("listIdsForMigration"), paramMap);
    }

    public int migrateResultByIds(List<Long> idList) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("idList", idList);
        return insert("migrateResultByIds", param);
    }

    public int deleteResultByIds(List<Long> detailIdList) {
        Map<String, Object> param = new HashMap<>(3);
        param.put("idList", detailIdList);
        return deleteBy("deleteResultByIds", param);
    }
    //endregion
}
