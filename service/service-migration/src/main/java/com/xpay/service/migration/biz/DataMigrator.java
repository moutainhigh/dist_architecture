package com.xpay.service.migration.biz;

import com.xpay.common.statics.dto.migrate.DataMigrationDto;
import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exceptions.BizException;
import com.xpay.common.util.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @Description: 数据迁移器
 * @author: chenyf
 * @Date: 2018/3/24
 */
public abstract class DataMigrator {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 默认记录的保留天数
     */
    private static final int DEFAULT_DATA_KEEP_DAYS = 15;
    /**
     * 默认每批次迁移的数量
     */
    private static final int DEFAULT_MIGRATION_NUM_EACH_TIME = 3000;

    /**
     * 启动数据迁移
     */
    public final void startDataMigration(DataMigrateTypeEnum migrateType) {
        logger.info("migrateType = {} 迁移开始", migrateType);
        if (migrateType == null) {
            throw new BizException("migrateType不能为空");
        }

        try {
            this.processDataMigration(migrateType);
        } catch (Throwable e) {
            logger.error("数据迁移过程中出现异常 migrateType = {}", migrateType, e);
        }
        logger.info("migrateType = {} 迁移结束", migrateType);
    }

    /**
     * 处理数据迁移
     */
    private void processDataMigration(DataMigrateTypeEnum migrateType) {
        logger.info("migrateType = {}", migrateType);

        //step1 计算，得到需要被执行迁移数据的截止日期
        Date migrationEndDate = this.calcMigrationEndDate(this.getDataKeepDays(migrateType));
        logger.info("migrateType={} migrationEndDate={}", migrateType, DateUtil.formatDate(migrationEndDate));


        //step2 得到需要执行迁移数据最大id
        long migrationMaxId = this.getMigrationMaxId(migrationEndDate, migrateType);
        logger.info("migrateType={} migrationEndDate={} migrationMaxId={}", migrateType, DateUtil.formatDate(migrationEndDate), migrationMaxId);

        long totalMigratedCount = 0;
        while (true) {
            DataMigrationDto migrationDto = new DataMigrationDto();
            migrationDto.setMaxId(migrationMaxId);
            migrationDto.setMigrateEndDate(migrationEndDate);
            migrationDto.setMigrateType(migrateType);
            migrationDto.setMigrateNumPerTime(getMigrationNumEachTime(migrateType));

            //执行迁移操作
            try {
                int curMigratedCount = this.doDataMigration(migrationDto);
                logger.info("migrateType={} migrationEndDate={} migrationNumEachTime={} curMigratedCount={}", migrateType, DateUtil.formatDate(migrationEndDate), migrationDto.getMigrateNumPerTime(), curMigratedCount);

                totalMigratedCount += curMigratedCount;
                if (curMigratedCount < migrationDto.getMigrateNumPerTime()) {
                    break;
                }
            } catch (Exception ex) {
                logger.error("数据迁移过程中出现异常 migrateType={} migrationEndDate={} migrationMaxId={}", migrateType, DateUtil.formatDate(migrationEndDate), migrationMaxId, ex);
                //todo 发送邮件之类
                break;
            }
        }
        logger.info("迁移任务结束 migrateType={} migrationEndDate={} migrationMaxId={} totalMigratedCount={}", migrateType, DateUtil.formatDate(migrationEndDate), migrationMaxId, totalMigratedCount);
    }

    /**
     * 获取执行迁移的数据库最大id
     *
     * @param migrationEndDate 迁移数据的最后日期（包含）
     * @param migrateTypeEnum  数据迁移类型
     * @return 迁移数据的最大数据库id
     */
    protected abstract long getMigrationMaxId(Date migrationEndDate, DataMigrateTypeEnum migrateTypeEnum);

    /**
     * 执行数据迁移
     *
     * @param migrationDto .
     * @return 返回迁移成功的数据条数
     */
    protected abstract int doDataMigration(DataMigrationDto migrationDto);

    /**
     * 取得数据需要保留的天数
     *
     * @return
     */
    protected int getDataKeepDays(DataMigrateTypeEnum migrateType) {
        return DEFAULT_DATA_KEEP_DAYS;
    }

    /**
     * 取得每次迁移数据的条数
     *
     * @return
     */
    protected int getMigrationNumEachTime(DataMigrateTypeEnum migrateType) {
        return DEFAULT_MIGRATION_NUM_EACH_TIME;
    }

    /**
     * 根据需要保留的天数计算迁移截止日期
     *
     * @param keepDays .
     * @return
     */
    private Date calcMigrationEndDate(int keepDays) {
        return DateUtil.convertDate(DateUtil.addDay(new Date(), (-1) * keepDays));
    }
}
