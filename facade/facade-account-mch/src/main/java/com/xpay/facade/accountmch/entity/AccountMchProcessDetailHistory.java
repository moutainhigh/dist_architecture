/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.accountmch.entity;

import com.xpay.common.statics.annotations.PK;
import com.xpay.common.statics.enums.merchant.MerchantTypeEnum;

import java.io.Serializable;

/**
 * 主商户账务处理明细归档表
 */
public class AccountMchProcessDetailHistory implements Serializable{

	private static final long serialVersionUID = 1L;

	@PK
	//columns START
	/**
	 * 自增主键
	 */
	private Long id;

	/**
	 * 版本号
	 */
	private Integer version;

	/**
	 * 创建时间
	 */
	private java.util.Date createTime;

	/**
	 * 商户编号
	 */
	private String merchantNo;

	/**
	 * 账户类型
	 * {@link MerchantTypeEnum}
	 */
	private Integer merchantType;

	/**
	 * 待清算余额
	 */
	private java.math.BigDecimal unsettleAmount;

	/**
	 * 可用余额
	 */
	private java.math.BigDecimal usableAmount;

	/**
	 * 变动金额(交易金额)
	 */
	private java.math.BigDecimal alterAmount;

	/**
	 * 待清算余额变动金额
	 */
	private java.math.BigDecimal alterUnsettleAmount;

	/**
	 * 可用余额变动金额
	 */
	private java.math.BigDecimal alterUsableAmount;

	/**
	 * 账务处理流水号
	 */
	private String accountProcessNo;

	/**
	 * 交易流水号
	 */
	private String trxNo;

	/**
	 * 商户订单号
	 */
	private String mchTrxNo;

	/**
	 * 订单交易时间
	 */
	private java.util.Date trxTime;

	/**
	 * 业务类型com.xpay.common.statics.enums.product.BusinessType
	 */
	private Integer bizType;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 账务处理类型
	 */
	private Integer processType;

	/**
	 * 扣款确认状态：1：待确认，2：确认扣款，3：确认退回，4：无须确认【若不是扣款的账务明细，设置为4】
	 */
	private Integer debitCommitStage;

	/**
	 * 附加信息
	 */
	private String extraInfo;

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
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getMerchantNo() {
		return this.merchantNo;
	}
	public void setMerchantType(Integer merchantType) {
		this.merchantType = merchantType;
	}
	public Integer getMerchantType() {
		return this.merchantType;
	}
	public void setUnsettleAmount(java.math.BigDecimal unsettleAmount) {
		this.unsettleAmount = unsettleAmount;
	}
	public java.math.BigDecimal getUnsettleAmount() {
		return this.unsettleAmount;
	}
	public void setUsableAmount(java.math.BigDecimal usableAmount) {
		this.usableAmount = usableAmount;
	}
	public java.math.BigDecimal getUsableAmount() {
		return this.usableAmount;
	}
	public void setAlterAmount(java.math.BigDecimal alterAmount) {
		this.alterAmount = alterAmount;
	}
	public java.math.BigDecimal getAlterAmount() {
		return this.alterAmount;
	}
	public void setAlterUnsettleAmount(java.math.BigDecimal alterUnsettleAmount) {
		this.alterUnsettleAmount = alterUnsettleAmount;
	}
	public java.math.BigDecimal getAlterUnsettleAmount() {
		return this.alterUnsettleAmount;
	}
	public void setAlterUsableAmount(java.math.BigDecimal alterUsableAmount) {
		this.alterUsableAmount = alterUsableAmount;
	}
	public java.math.BigDecimal getAlterUsableAmount() {
		return this.alterUsableAmount;
	}
	public void setAccountProcessNo(String accountProcessNo) {
		this.accountProcessNo = accountProcessNo;
	}
	public String getAccountProcessNo() {
		return this.accountProcessNo;
	}
	public void setTrxNo(String trxNo) {
		this.trxNo = trxNo;
	}
	public String getTrxNo() {
		return this.trxNo;
	}
	public void setMchTrxNo(String mchTrxNo) {
		this.mchTrxNo = mchTrxNo;
	}
	public String getMchTrxNo() {
		return this.mchTrxNo;
	}
	public void setTrxTime(java.util.Date trxTime) {
		this.trxTime = trxTime;
	}
	public java.util.Date getTrxTime() {
		return this.trxTime;
	}
	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}

	public Integer getBizType() {
		return this.bizType;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setProcessType(Integer processType) {
		this.processType = processType;
	}

	public Integer getProcessType() {
		return this.processType;
	}

	public void setDebitCommitStage(Integer debitCommitStage) {
		this.debitCommitStage = debitCommitStage;
	}

	public Integer getDebitCommitStage() {
		return this.debitCommitStage;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public String getExtraInfo() {
		return this.extraInfo;
	}
	public void setMigrateTime(java.util.Date migrateTime) {
		this.migrateTime = migrateTime;
	}
	public java.util.Date getMigrateTime() {
		return this.migrateTime;
	}

}
