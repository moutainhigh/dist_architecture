/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import java.io.Serializable;

/**
 * longfenghua
 * 数据字典表
 */
public class DataDictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * ID
     */
    private Long id;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * json类型数据字典信息
     */
    private String dataInfo;

    /**
     * 描述
     */
    private String remark;

    /**
     * createTime
     */
    private java.util.Date createTime;

    /**
     * modifyTime
     */
    private java.util.Date modifyTime;

    /**
     * modifyOperator
     */
    private String modifyOperator;

    /**
     * 系统标识 {@link com.xpay.common.statics.enums.user.pms.SystemTypeEnum}
     */
    private Integer systemType;

    //columns END


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataName() {
        return this.dataName;
    }

    public void setDataInfo(String dataInfo) {
        this.dataInfo = dataInfo;
    }

    public String getDataInfo() {
        return this.dataInfo;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setModifyTime(java.util.Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public java.util.Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyOperator(String modifyOperator) {
        this.modifyOperator = modifyOperator;
    }

    public String getModifyOperator() {
        return this.modifyOperator;
    }

    public Integer getSystemType() {
        return systemType;
    }

    public void setSystemType(Integer systemType) {
        this.systemType = systemType;
    }
}
