package com.xpay.web.pms.vo.permission;

/**
 * 操作员查询参数
 *
 * @author longfenghua
 * @date 2019/11/1
 */
public class PmsDataDictionaryVO {
    /**
     * 数据名称
     */
    private String dataName;
    /**
     * 系统标识
     */
    private Integer systemType;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getSystemType() {
        return systemType;
    }

    public void setSystemType(Integer systemType) {
        this.systemType = systemType;
    }
}
