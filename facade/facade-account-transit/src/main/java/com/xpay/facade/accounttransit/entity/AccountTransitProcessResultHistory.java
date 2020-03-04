package com.xpay.facade.accounttransit.entity;

import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;

/**
 * 在途账户账务处理结果归档表
 */
public class AccountTransitProcessResultHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    @PK
    private Long id;
    private Integer version;
    private java.util.Date createTime;

    /**
     * 账务处理流水号
     */
    private String accountProcessNo;

    /**
     * 处理结果(1=成功 -1=失败)
     */
    private Integer processResult;

    /**
     * 错误码(0=无异常 1=系统异常 其他=具体业务异常)
     */
    private Integer errorCode;

    /**
     * 审核阶段(1=不审核 2=待审核 3=已审核)
     */
    private Integer auditStage;

    /**
     * 回调阶段(1=待回调 2=已回调 3=不回调)
     */
    private Integer callbackStage;

    /**
     * 备注/异常描述
     */
    private String remark;

    /**
     * 是否来自异步账务处理(1=是 -1=否)
     */
    private Integer isFromAsync;

    /**
     * 账务请求数据(JSON格式)
     */
    private String requestDto;

    /**
     * 账务处理的数据(JSON格式)
     */
    private String processDtoList;

    /**
     * 迁移时间
     */
    private java.util.Date migrateTime;

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

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setAccountProcessNo(String accountProcessNo) {
        this.accountProcessNo = accountProcessNo;
    }

    public String getAccountProcessNo() {
        return this.accountProcessNo;
    }

    public void setProcessResult(Integer processResult) {
        this.processResult = processResult;
    }

    public Integer getProcessResult() {
        return this.processResult;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public void setAuditStage(Integer auditStage) {
        this.auditStage = auditStage;
    }

    public Integer getAuditStage() {
        return this.auditStage;
    }

    public void setCallbackStage(Integer callbackStage) {
        this.callbackStage = callbackStage;
    }

    public Integer getCallbackStage() {
        return this.callbackStage;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setIsFromAsync(Integer isFromAsync) {
        this.isFromAsync = isFromAsync;
    }

    public Integer getIsFromAsync() {
        return this.isFromAsync;
    }

    public void setRequestDto(String requestDto) {
        this.requestDto = requestDto;
    }

    public Object getRequestDto() {
        return this.requestDto;
    }

    public void setProcessDtoList(String processDtoList) {
        this.processDtoList = processDtoList;
    }

    public Object getProcessDtoList() {
        return this.processDtoList;
    }

    public void setMigrateTime(java.util.Date migrateTime) {
        this.migrateTime = migrateTime;
    }

    public java.util.Date getMigrateTime() {
        return this.migrateTime;
    }

}
