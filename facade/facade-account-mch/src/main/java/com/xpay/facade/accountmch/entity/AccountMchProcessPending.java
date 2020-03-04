/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.accountmch.entity;

import com.xpay.common.statics.annotations.PK;
import com.xpay.common.util.utils.BeanUtil;

import java.io.Serializable;

/**
 * 主商户待账务处理表
 */
public class AccountMchProcessPending implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * 自增主键
     */
    @PK
    private Long id;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 修改时间
     */
    private java.util.Date modifyTime;

    /**
     * 账务处理流水号
     */
    private String accountProcessNo;

    /**
     * 处理阶段(1=待处理 2=处理中 3=已处理)
     */
    private Integer processStage;

    /**
     * 账务处理数据的唯一编码
     */
    private String dataUnqKey;

    /**
     * 账务请求数据(JSON格式)
     */
    private String requestDto;

    /**
     * 账务处理数据(JSON LIST格式)
     */
    private String processDtoList;

    /**
     * 备注
     */
    private String remark;

    //columns END
    //region 非数据库字段
    /**
     * 数据是否在历史表
     */
    private boolean inHistory;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setModifyTime(java.util.Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public java.util.Date getModifyTime() {
        return this.modifyTime;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public String getAccountProcessNo() {
        return this.accountProcessNo;
    }

    public void setProcessStage(Integer processStage) {
        this.processStage = processStage;
    }

    public Integer getProcessStage() {
        return this.processStage;
    }

    public void setDataUnqKey(String dataUnqKey) {
        this.dataUnqKey = dataUnqKey;
    }

    public String getDataUnqKey() {
        return this.dataUnqKey;
    }

    public void setRequestDto(String requestDto) {
        this.requestDto = requestDto;
    }

    public String getRequestDto() {
        return this.requestDto;
    }

    public void setProcessDtoList(String processDtoList) {
        this.processDtoList = processDtoList;
    }

    public String getProcessDtoList() {
        return this.processDtoList;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

    public boolean isInHistory() {
        return inHistory;
    }

    /**
     * 根据待账务处理历史创建一个待账务处理实体
     *
     * @param history 待账务处理历史
     * @return .
     */
    public static AccountMchProcessPending newFromHistory(AccountMchProcessPendingHistory history) {
        if (history == null) {
            return null;
        } else {
            AccountMchProcessPending pending = new AccountMchProcessPending();
            BeanUtil.copyProperties(history, pending);
            pending.inHistory = true; //赋值需要在copyProperties后面
            return pending;
        }
    }
}
