package com.xpay.service.migration.biz;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.facade.accountmch.service.AccountMchMigrationFacade;
import com.xpay.service.migration.conts.MigratorName;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component(MigratorName.ACCOUNT_MCH_MIGRATION)
public class AccountMchMigrateBiz extends DataMigrator {
    @Reference(check = false)
    AccountMchMigrationFacade accountMchMigrationFacade;


    @Override
    protected long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateTypeEnum) {
        return accountMchMigrationFacade.getMigrationMaxId(migrationEndDate, migrateTypeEnum);
    }

    @Override
    protected int doDataMigration(DataMigrationDto migrationDto) {
        return accountMchMigrationFacade.doDataMigration(migrationDto);
    }

    @Override
    protected int getDataKeepDays(DataMigrateTypeEnum migrateType) {
        switch (migrateType) {
            case ACCOUNT_MCH_PROCESS_PENDING:
            case ACCOUNT_MCH_PROCESS_RESULT:
                return 3;
            case ACCOUNT_MCH_COMMON_UNIQUE:
                return 30;
            default:
                logger.error("不支持的migrateType类型{}", migrateType);
                throw new BizException("不支持的migrateType类型");
        }
    }

}
