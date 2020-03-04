package com.xpay.service.accountmch.facade;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.facade.accountmch.service.AccountMchMigrationFacade;
import com.xpay.service.accountmch.biz.AccountMchMigrationBiz;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author luobinzhao
 * @date 2020/1/15 18:59
 */
@Service
public class AccountMchMigrationFacadeImpl implements AccountMchMigrationFacade {
    @Autowired
    private AccountMchMigrationBiz accountMchMigrationBiz;

    @Override
    public long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateType) {
        return accountMchMigrationBiz.getMigrationMaxId(migrationEndDate, migrateType);
    }

    @Override
    public int doDataMigration(DataMigrationDto migrationDto) {
        return accountMchMigrationBiz.doDataMigration(migrationDto);
    }

}
