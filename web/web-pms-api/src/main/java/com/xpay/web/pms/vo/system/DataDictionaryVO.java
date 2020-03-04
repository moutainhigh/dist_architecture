/*
 * Powered By [joinPay.com]
 */
package com.xpay.web.pms.vo.system;

import com.xpay.common.util.utils.JsonUtil;
import com.xpay.facade.config.entity.DataDictionary;

import java.util.Date;
import java.util.List;

/**
 * 数据字典VO
 */
public class DataDictionaryVO {

    private static final long serialVersionUID = 1L;


    private Long id;
    private String dataName;
    private List<Item> dataInfo;
    private String remark;
    private java.util.Date createTime;
    private java.util.Date modifyTime;
    private String creator;
    private String modifyOperator;

    /**
     * 系统标识 {@link com.xpay.common.statics.enums.user.pms.SystemTypeEnum}
     */
    private Integer systemType;

    public DataDictionaryVO() {
    }

    public DataDictionaryVO(DataDictionary dataDictionary) {
        this.id = dataDictionary.getId();
        this.dataName = dataDictionary.getDataName();
        this.dataInfo = JsonUtil.toList(dataDictionary.getDataInfo(), Item.class);
        this.remark = dataDictionary.getRemark();
        this.createTime = dataDictionary.getCreateTime();
        this.modifyTime = dataDictionary.getModifyTime();
        this.creator = dataDictionary.getCreator();
        this.modifyOperator = dataDictionary.getModifyOperator();
        this.systemType = dataDictionary.getSystemType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public List<Item> getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(List<Item> dataInfo) {
        this.dataInfo = dataInfo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifyOperator() {
        return modifyOperator;
    }

    public void setModifyOperator(String modifyOperator) {
        this.modifyOperator = modifyOperator;
    }

    public Integer getSystemType() {
        return systemType;
    }

    public void setSystemType(Integer systemType) {
        this.systemType = systemType;
    }

    public static class Item {
        private String flag;
        private String code;
        private String desc;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Item() {
        }
    }

}
