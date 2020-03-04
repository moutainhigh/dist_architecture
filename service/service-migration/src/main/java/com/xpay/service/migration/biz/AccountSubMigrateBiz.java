package com.xpay.service.migration.biz;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.facade.accountsub.service.AccountSubMigrationFacade;
import com.xpay.service.migration.conts.MigratorName;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component(MigratorName.ACCOUNT_SUB_MIGRATION)
public class AccountSubMigrateBiz extends DataMigrator {
    @Reference(check = false)
    AccountSubMigrationFacade accountSubMigrationFacade;


    @Override
    protected long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateTypeEnum) {
        return accountSubMigrationFacade.getMigrationMaxId(migrationEndDate, migrateTypeEnum);
    }

    @Override
    protected int doDataMigration(DataMigrationDto migrationDto) {
        return accountSubMigrationFacade.doDataMigration(migrationDto);
    }

    @Override
    protected int getDataKeepDays(DataMigrateTypeEnum migrateType) {
        switch (migrateType) {
            case ACCOUNT_SUB_PROCESS_PENDING:
            case ACCOUNT_SUB_PROCESS_RESULT:
                return 3;
            case ACCOUNT_SUB_COMMON_UNIQUE:
                return 30;
            default:
                logger.error("不支持的migrateType类型{}", migrateType);
                throw new BizException("不支持的migrateType类型");
        }
    }

}
