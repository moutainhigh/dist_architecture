package com.xpay.service.accounttransit.biz;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exception.CommonExceptions;
import com.xpay.common.util.utils.DateUtil;
import com.xpay.common.util.utils.JsonUtil;
import com.xpay.service.accounttransit.dao.AccountTransitCommonUniqueDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessDetailDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessPendingDao;
import com.xpay.service.accounttransit.dao.AccountTransitProcessResultDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Author: chenyf
 * Date: 2020.1.13
 * Time: 14:55
 * Description: 在途账户账务数据迁移业务
 */
@Component
public class AccountTransitMigrationBiz {
    private Logger logger = LoggerFactory.getLogger(AccountTransitMigrationBiz.class);
    @Autowired
    private AccountTransitProcessDetailDao accountTransitProcessDetailDao;
    @Autowired
    private AccountTransitProcessPendingDao accountTransitProcessPendingDao;
    @Autowired
    private AccountTransitProcessResultDao accountTransitProcessResultDao;
    @Autowired
    private AccountTransitCommonUniqueDao accountTransitCommonUniqueDao;


    /**
     * 获取执行迁移的数据库最大id
     *
     * @param migrationEndDate 迁移数据的最后日期（包含）
     * @return 迁移数据的最大数据库id
     * todo 这是需要处理唯一约束表的
     */
    public long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateType) {
        if (migrationEndDate == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("migrationEndDate不能为null");
        } else if (migrateType == null) {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("migrateType不能为null");
        }

        if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_DETAIL) {
            return accountTransitProcessDetailDao.getMigrationMaxId(migrationEndDate);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_PENDING) {
            return accountTransitProcessPendingDao.getMigrationMaxId(migrationEndDate);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_RESULT) {
            return accountTransitProcessResultDao.getMigrationMaxId(migrationEndDate);
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
        if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_DETAIL) {
            idList = accountTransitProcessDetailDao.listIdsForMigration(migrationDto);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_PENDING) {
            idList = accountTransitProcessPendingDao.listIdsForMigration(migrationDto);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_RESULT) {
            idList = accountTransitProcessResultDao.listIdsForMigration(migrationDto);
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_COMMON_UNIQUE) {
            idList = accountTransitCommonUniqueDao.listIdsForMigration(migrationDto);
        } else {
            throw CommonExceptions.PARAM_INVALID.newWithErrMsg("未支持的migrateType: " + migrateType.name());
        }

        logger.info("migrateType={} migrationEndDate={} idList.size={} 获取id完成", migrateType, DateUtil.formatDateTime(migrationDto.getMigrateEndDate()), idList == null ? 0 : idList.size());
        if (idList == null || idList.size() <= 0) {
            return 0;
        }

        int insertCount = 0, deleteCount = 0;
        if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_DETAIL) {
            insertCount = accountTransitProcessDetailDao.migrateDetailByIds(idList);
            if (insertCount > 0) {
                deleteCount = accountTransitProcessDetailDao.deleteDetailByIds(idList);
            }
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_PENDING) {
            insertCount = accountTransitProcessPendingDao.migratePendingByIds(idList);
            if (insertCount > 0) {
                deleteCount = accountTransitProcessPendingDao.deletePendingByIds(idList);
            }
        } else if (migrateType == DataMigrateTypeEnum.ACCOUNT_TRANSIT_PROCESS_RESULT) {
            insertCount = accountTransitProcessResultDao.migrateResultByIds(idList);
            if (insertCount > 0) {
                deleteCount = accountTransitProcessResultDao.deleteResultByIds(idList);
            }
        } else {
            accountTransitCommonUniqueDao.deleteUniqueByIds(idList);
        }

        //如果两边不一致，则抛出异常让当前事务回滚
        if (insertCount > 0 && insertCount != deleteCount) {
            logger.error("insertCount={} deleteCount={} DataMigrationDto={}", insertCount, deleteCount, JsonUtil.toStringPretty(migrationDto));
            throw CommonExceptions.BIZ_INVALID.newWithErrMsg("数据迁移时插入记录数与删除记录数不一致");
        }
        return insertCount;
    }


}
