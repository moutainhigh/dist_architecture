package com.xpay.service.accountmch.biz;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.service.accountmch.dao.AccountMchCommonUniqueDao;
import com.xpay.service.accountmch.dao.AccountMchProcessDetailDao;
import com.xpay.service.accountmch.dao.AccountMchProcessPendingDao;
import com.xpay.service.accountmch.dao.AccountMchProcessResultDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author luobinzhao
 * @date 2020/1/15 19:00
 */
@Component
public class AccountMchMigrationBiz {
    private Logger logger = LoggerFactory.getLogger(AccountMchMigrationBiz.class);
    @Autowired
    private AccountMchProcessDetailDao accountMchProcessDetailDao;
    @Autowired
    private AccountMchProcessPendingDao accountMchProcessPendingDao;
    @Autowired
    private AccountMchProcessResultDao accountMchProcessResultDao;
    @Autowired
    private AccountMchCommonUniqueDao accountMchCommonUniqueDao;


    /**
     * 获取执行迁移的数据库最大id
     *
     * @param migrationEndDate 迁移数据的最后日期（包含）
     * @return 迁移数据的最大数据库id
     */
    public long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateType) {
        if (migrationEndDate == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("migrationEndDate不能为null");
        } else if (migrateType == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("migrateType不能为null");
        }

        if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_DETAIL) {
            return accountMchProcessDetailDao.getMigrationMaxId(migrationEndDate);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_PENDING) {
            return accountMchProcessPendingDao.getMigrationMaxId(migrationEndDate);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_RESULT) {
            return accountMchProcessResultDao.getMigrationMaxId(migrationEndDate);
        } else {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("未支持的migrateType: " + migrateType.name());
        }

    }

    /**
     * 执行数据迁移
     *
     * @param migrationDto .
     * @return .
     */
    @Transactional(rollbackFor = Exception.class)
    public int doDataMigration(DataMigrationDto migrationDto) {
        DataMigrationDto.validateMigrationParam(migrationDto);
        DataMigrateTypeEnum migrateType = migrationDto.getMigrateType();

        List<Long> idList;
        if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_DETAIL) {
            idList = accountMchProcessDetailDao.listIdsForMigration(migrationDto);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_PENDING) {
            idList = accountMchProcessPendingDao.listIdsForMigration(migrationDto);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_RESULT) {
            idList = accountMchProcessResultDao.listIdsForMigration(migrationDto);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_COMMON_UNIQUE) {
            idList = accountMchCommonUniqueDao.listIdsForMigration(migrationDto);
        } else {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("未支持的migrateType: " + migrateType.name());
        }

        logger.info("migrateType={} migrationEndDate={} idList.size={} 获取id完成", migrateType, DateUtil.formatDateTime(migrationDto.getMigrateEndDate()), idList == null ? 0 : idList.size());
        if (idList == null || idList.size() <= 0) {
            return 0;
        }

        int insertCount = 0, deleteCount = 0;
        if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_DETAIL) {
            insertCount = accountMchProcessDetailDao.migrateDetailByIds(idList);
            if (insertCount > 0) {
                deleteCount = accountMchProcessDetailDao.deleteDetailByIds(idList);
            }
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_PENDING) {
            insertCount = accountMchProcessPendingDao.migratePendingByIds(idList);
            if (insertCount > 0) {
                deleteCount = accountMchProcessPendingDao.deletePendingByIds(idList);
            }
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_MCH_PROCESS_RESULT) {
            insertCount = accountMchProcessResultDao.migrateResultByIds(idList);
            if (insertCount > 0) {
                deleteCount = accountMchProcessResultDao.deleteResultByIds(idList);
            }
        } else {
            accountMchCommonUniqueDao.deleteUniqueByIds(idList);
        }

        //如果两边不一致，则抛出异常让当前事务回滚
        if (insertCount > 0 && insertCount != deleteCount) {
            logger.error("insertCount={} deleteCount={} DataMigrationDto={}", insertCount, deleteCount, JsonUtil.toStringPretty(migrationDto));
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("数据迁移时插入记录数与删除记录数不一致");
        }
        return insertCount;
    }

}
