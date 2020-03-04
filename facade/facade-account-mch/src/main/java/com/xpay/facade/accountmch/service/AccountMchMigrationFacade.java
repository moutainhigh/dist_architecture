package com.xpay.facade.accountmch.service;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exception.BizException;

import java.util.Date;

/**
 * @author luobinzhao
 * @date 2020/1/15 17:08
 */
public interface AccountMchMigrationFacade {
    /**
     * 获取执行迁移的数据库最大id
     *
     * @param migrationEndDate 迁移数据的最后日期（包含）
     * @param migrateType      数据迁移类型
     * @return 迁移数据的最大数据库id
     */
    long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateType) throws BizException;


    /**
     * 执行数据迁移
     *
     * @param migrationDto
     * @return
     */
    int doDataMigration(DataMigrationDto migrationDto) throws BizException;
}
