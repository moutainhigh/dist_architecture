package com.xpay.common.statics.dto.migrate;

import com.xpay.common.statics.enums.migrate.DataMigrateTypeEnum;
import com.xpay.common.statics.exceptions.BizException;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2020.1.14
 * Time: 16:24
 * Description: chenyf
 */
public class DataMigrationDto implements Serializable {
    private static final long serialVersionUID = -1636546564645311315L;

    /**
     * 数据迁移类型
     *
     * @see DataMigrateTypeEnum
     */
    private DataMigrateTypeEnum migrateType;

    /**
     * 最后迁移日期(包含)
     */
    private Date migrateEndDate;

    /**
     * 每批迁移数量
     */
    private long migrateNumPerTime;

    /**
     * 迁移任务最大ID
     */
    private long maxId;


    public DataMigrateTypeEnum getMigrateType() {
        return migrateType;
    }

    public void setMigrateType(DataMigrateTypeEnum migrateType) {
        this.migrateType = migrateType;
    }

    public Date getMigrateEndDate() {
        return migrateEndDate;
    }

    public void setMigrateEndDate(Date migrateEndDate) {
        this.migrateEndDate = migrateEndDate;
    }

    public long getMigrateNumPerTime() {
        return migrateNumPerTime;
    }

    public void setMigrateNumPerTime(long migrateNumPerTime) {
        this.migrateNumPerTime = migrateNumPerTime;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public static void validateMigrationParam(DataMigrationDto migrationDto) {
        if (migrationDto == null) {
            throw new BizException(BizException.PARAM_INVALIDATE, "migrationDto不能为null");
        } else if (migrationDto.getMigrateEndDate() == null) {
            throw new BizException(BizException.PARAM_INVALIDATE, "migrateEndDate不能为null");
        }
    }
}
