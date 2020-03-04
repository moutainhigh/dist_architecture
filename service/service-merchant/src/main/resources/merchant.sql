CREATE TABLE `tbl_merchant`
(
    `ID`                 bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `CREATE_TIME`        datetime            NOT NULL COMMENT '创建时间',
    `VERSION`            bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '版本号',
    `MODIFY_TIME`        datetime            NOT NULL COMMENT '修改时间',
    `MERCHANT_NO`        varchar(30)         NOT NULL COMMENT '商户编号',
    `PARENT_MERCHANT_NO` varchar(30)         NULL COMMENT '父商户编号(如果是子商户时填写)',
    `STATUS`             tinyint(3)          NOT NULL COMMENT '商户状态com.xpay.common.statics.enums.merchant.MerchantStatusEnum',
    `AUTH_STATUS`        tinyint(3)          NOT NULL COMMENT '商户认证状态MerchantAuthStatusEnum',
    `MERCHANT_TYPE`      tinyint(3)          NOT NULL COMMENT '商户类型:1:平台商户，2:子商户',
    `FULL_NAME`          varchar(90)                  DEFAULT NULL COMMENT '公司全称',
    `SHORT_NAME`         varchar(30)                  DEFAULT NULL COMMENT '公司简称',
    `MERCHANT_LEVEL`     tinyint(3)                   DEFAULT NULL COMMENT '商户等级,1-A,2-B,3-C',
    PRIMARY KEY (`ID`),
    UNIQUE KEY `UK_MERCHANT_NO` (`MERCHANT_NO`),
    KEY `IDX_PARENT_MERCHANT_NO` (`PARENT_MERCHANT_NO`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    comment '商户表';

CREATE TABLE `tbl_merchant_detail`
(
    `ID`                      bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `CREATE_TIME`             datetime            NOT NULL COMMENT '创建时间',
    `VERSION`                 bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '版本号',
    `MODIFY_TIME`             datetime            NOT NULL COMMENT '修改时间',
    `MERCHANT_NO`             varchar(30)         NOT NULL COMMENT '商户编号',

    `ADDRESS`                 varchar(150)                 DEFAULT NULL COMMENT '公司地址',
    `URL`                     varchar(150)                 DEFAULT NULL COMMENT '公司网址',
    `ICP`                     varchar(50)                  DEFAULT NULL COMMENT 'ICP证备案号',
    `TELEPHONE`               varchar(20)                  DEFAULT NULL COMMENT '固定电话',
    `FAX`                     varchar(20)                  DEFAULT NULL COMMENT '传真号',
    `IP_SEG`                  varchar(255)                 DEFAULT NULL COMMENT 'IP段',
    `BUSINESS_AREA`           smallint(5)                  DEFAULT NULL COMMENT '经营面积',
    `EMP_NUM`                 smallint(5)                  DEFAULT NULL COMMENT '员工数量',
    `BUSINESS_SCOPE`          varchar(150)                 DEFAULT NULL COMMENT '经营范围',
    `INDUSTRY_TYPE`           smallint(5)                  DEFAULT NULL COMMENT '行业类别',
    `ESTABLISH_DATE`          date                         DEFAULT NULL COMMENT '公司成立日期',


    `LEGAL_PERSON`            varchar(30)                  DEFAULT NULL COMMENT '法人姓名',
    `LEGAL_PERSON_ID`         varchar(100)                 DEFAULT NULL COMMENT '法人身份证号码',
    `LEGAL_PERSON_ID_EXPIRE`  date                         DEFAULT NULL COMMENT '法人身份证有效期',
    `LEGAL_PERSON_ID_ADDRESS` varchar(200)                 DEFAULT NULL COMMENT '法人身份证地址',
    `LEGAL_PERSON_LOCATION`   tinyint(3)                   DEFAULT NULL COMMENT '法定代表人归属地。参考MerchantLocationEnum',

    `LICENSE_NO`              varchar(50)                  DEFAULT NULL COMMENT '营业执照号',
    `LICENSE_NO_EXPIRE`       date                         DEFAULT NULL COMMENT '营业执照有效期',
    `REGISTRY_TYPE`           tinyint(3)          NOT NULL COMMENT '工商登记类型(个人，企业，个体,MerchantRegistryTypeEnum)',
    `REGISTER_AMOUNT`         decimal(12, 4)               DEFAULT NULL COMMENT '注册资本(万元)',
    `ORG_CODE`                varchar(50)                  DEFAULT NULL COMMENT '组织机构代码',
    `NATIONAL_TAX_NO`         varchar(100)                 DEFAULT NULL COMMENT '税务登记证(国税)',
    `LAND_TAX_NO`             varchar(100)                 DEFAULT NULL COMMENT '税务登记证(地税)',


    `SIGN_TIME`               datetime            NULL COMMENT '签约时间',
    `CONTRACT_EXPIRE`         date                NULL COMMENT '合同到期日',

    `BUSI_CONTACT_NAME`       varchar(30)                  DEFAULT NULL COMMENT '业务联系人',
    `BUSI_CONTACT_MOBILE_NO`  varchar(15)                  DEFAULT NULL COMMENT '业务联系手机号码',
    `BUSI_CONTACT_EMAIL`      varchar(50)                  DEFAULT NULL COMMENT '业务联系邮箱',
    `BUSI_CONTACT_QQ`         varchar(15)                  DEFAULT NULL COMMENT '业务联系联系QQ',

    `TECH_CONTACT_NAME`       varchar(30)                  DEFAULT NULL COMMENT '技术联系人',
    `TECH_CONTACT_MOBILE_NO`  varchar(15)                  DEFAULT NULL COMMENT '技术联系手机号',
    `TECH_CONTACT_EMAIL`      varchar(50)                  DEFAULT NULL COMMENT '技术联系邮箱',
    `TECH_CONTACT_QQ`         varchar(15)                  DEFAULT NULL COMMENT '技术联系QQ',

    `IS_DELETE`               tinyint(3)                   DEFAULT '0' COMMENT '是否删除',
    `REMARK`                  varchar(200)                 DEFAULT NULL COMMENT '备注',

    PRIMARY KEY (`ID`),
    UNIQUE KEY `UK_MERCHANT_NO` (`MERCHANT_NO`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
    comment '商户信息详情表';

