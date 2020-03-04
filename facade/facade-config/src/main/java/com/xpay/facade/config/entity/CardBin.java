/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import com.xpay.common.statics.annotations.PK;
import java.io.Serializable;

/**
 * 卡宾表
 */
public class CardBin implements Serializable {
	private static final long serialVersionUID = 1L;

	//columns START
	/**
	 * 自增id
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
	 * 卡BIN
	 */
	private String cardBin;
	/**
	 * 卡名
	 */
	private String cardName;
	/**
	 * 卡种:1借记卡,2贷记卡,3准贷记卡,4预付费卡
	 */
	private Integer cardKind;
	/**
	 * 卡片长度
	 */
	private Integer cardLength;
	/**
	 * 发卡行代码
	 */
	private String bankCode;
	/**
	 * 发卡行名称
	 */
	private String bankName;
	/**
	 * 银行简称
	 */
	private String bankShortName;
	/**
	 * 状态:101无效、100有效
	 */
	private Integer status;
	//columns END


	/**
	 * 自增id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 自增id
	 */
	public Long getId() {
		return this.id;
	}
	/**
	 * 创建时间
	 */
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 创建时间
	 */
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	/**
	 * 版本号
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * 版本号
	 */
	public Integer getVersion() {
		return this.version;
	}
	/**
	 * 卡BIN
	 */
	public void setCardBin(String cardBin) {
		this.cardBin = cardBin;
	}
	/**
	 * 卡BIN
	 */
	public String getCardBin() {
		return this.cardBin;
	}
	/**
	 * 卡名
	 */
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	/**
	 * 卡名
	 */
	public String getCardName() {
		return this.cardName;
	}
	/**
	 * 卡种:1借记卡,2贷记卡,3准贷记卡,4预付费卡
	 */
	public void setCardKind(Integer cardKind) {
		this.cardKind = cardKind;
	}
	/**
	 * 卡种:1借记卡,2贷记卡,3准贷记卡,4预付费卡
	 */
	public Integer getCardKind() {
		return this.cardKind;
	}
	/**
	 * 卡片长度
	 */
	public void setCardLength(Integer cardLength) {
		this.cardLength = cardLength;
	}
	/**
	 * 卡片长度
	 */
	public Integer getCardLength() {
		return this.cardLength;
	}
	/**
	 * 发卡行代码
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	 * 发卡行代码
	 */
	public String getBankCode() {
		return this.bankCode;
	}
	/**
	 * 发卡行名称
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * 发卡行名称
	 */
	public String getBankName() {
		return this.bankName;
	}
	/**
	 * 银行简称
	 */
	public void setBankShortName(String bankShortName) {
		this.bankShortName = bankShortName;
	}
	/**
	 * 银行简称
	 */
	public String getBankShortName() {
		return this.bankShortName;
	}
	/**
	 * 状态:101无效、100有效
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 状态:101无效、100有效
	 */
	public Integer getStatus() {
		return this.status;
	}

}
