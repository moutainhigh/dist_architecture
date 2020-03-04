package com.xpay.service.accountsub.facade;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.facade.accountsub.service.AccountSubMigrationFacade;
import com.xpay.service.accountsub.biz.AccountSubMigrationBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Author: Cmf
 * Date: 2020.1.13
 * Time: 14:54
 * Description:
 */
@Service
public class AccountSubMigrationFacadeImpl implements AccountSubMigrationFacade {
    @Autowired
    private AccountSubMigrationBiz accountSubMigrationBiz;

    @Override
    public long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateType) {
        return accountSubMigrationBiz.getMigrationMaxId(migrationEndDate, migrateType);
    }

    @Override
    public int doDataMigration(DataMigrationDto migrationDto) {
        return accountSubMigrationBiz.doDataMigration(migrationDto);
    }

}
