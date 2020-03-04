CREATE TABLE `tbl_global_lock`
(
    `ID`              bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `VERSION`         bigint(20)          NOT NULL DEFAULT '0' COMMENT '版本号',
    `CREATE_TIME`     datetime            NOT NULL COMMENT '创建时间',
    `RESOURCE_ID`     char(32)            NOT NULL COMMENT '资源id',
    `RESOURCE_STATUS` smallint(6)         NOT NULL COMMENT '资源状态(1=空闲 2=锁定)',
    `CLIENT_ID`       varchar(32)         NOT NULL DEFAULT '' COMMENT '客户端id(锁持有者)',
    `CLIENT_FLAG`     varchar(50)                  DEFAULT '' COMMENT '客户端标识',
    `LOCK_TIME`       datetime                     DEFAULT NULL COMMENT '上锁时间',
    `EXPIRE_TIME`     datetime                     DEFAULT NULL COMMENT '过期时间(RESOURCE_STATUS=2且EXPIRE_TIME=NULL时表示永不过期)',
    PRIMARY KEY (`ID`),
    UNIQUE KEY `uk_resource_id` (`RESOURCE_ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='并发控制锁'

