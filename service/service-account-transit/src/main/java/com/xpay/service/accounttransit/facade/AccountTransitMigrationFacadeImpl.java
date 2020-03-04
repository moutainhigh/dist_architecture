package com.xpay.service.accounttransit.facade;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.facade.accounttransit.service.AccountTransitMigrationFacade;
import com.xpay.service.accounttransit.biz.AccountTransitMigrationBiz;
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
public class AccountTransitMigrationFacadeImpl implements AccountTransitMigrationFacade {
    @Autowired
    private AccountTransitMigrationBiz accountTransitMigrationBiz;

    @Override
    public long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateType) {
        return accountTransitMigrationBiz.getMigrationMaxId(migrationEndDate, migrateType);
    }

    @Override
    public int doDataMigration(DataMigrationDto migrationDto) {
        return accountTransitMigrationBiz.doDataMigration(migrationDto);
    }

}
