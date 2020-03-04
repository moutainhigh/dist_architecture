/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import com.xpay.common.statics.annotations.PK;
import java.io.Serializable;

/**
 * 银行列表
 */
public class BankList implements Serializable {
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
	 * 银行简称
	 */
	private String shortName;
	/**
	 * 银行全称
	 */
	private String fullName;
	/**
	 * 银行类型
	 */
	private Integer bankType;
	/**
	 * 支持业务类型
	 */
	private String tradeType;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 网联机构编码
	 */
	private String nuccIdentify;
	/**
	 * B2B渠道编码
	 */
	private String b2bCode;
	/**
	 * B2C渠道编码
	 */
	private String b2cCode;
	/**
	 * 是否支持B2B
	 */
	private Boolean isB2b;
	/**
	 * 是否支持B2C
	 */
	private Boolean isB2c;
	/**
	 * 是否支持代扣
	 */
	private Boolean isCharge;
	/**
	 * 是否支持代付
	 */
	private Boolean isPay;
	/**
	 * 是否支持鉴权
	 */
	private Boolean isAuth;
	/**
	 * 是否支持快捷支付
	 */
	private Boolean isFastpay;
	/**
	 * 备注
	 */
	private String remark;
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
	 * 银行简称
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	/**
	 * 银行简称
	 */
	public String getShortName() {
		return this.shortName;
	}
	/**
	 * 银行全称
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * 银行全称
	 */
	public String getFullName() {
		return this.fullName;
	}
	/**
	 * 银行类型
	 */
	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}
	/**
	 * 银行类型
	 */
	public Integer getBankType() {
		return this.bankType;
	}
	/**
	 * 支持业务类型
	 */
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	/**
	 * 支持业务类型
	 */
	public String getTradeType() {
		return this.tradeType;
	}
	/**
	 * 状态
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 状态
	 */
	public Integer getStatus() {
		return this.status;
	}
	/**
	 * 网联机构编码
	 */
	public void setNuccIdentify(String nuccIdentify) {
		this.nuccIdentify = nuccIdentify;
	}
	/**
	 * 网联机构编码
	 */
	public String getNuccIdentify() {
		return this.nuccIdentify;
	}
	/**
	 * B2B渠道编码
	 */
	public void setB2bCode(String b2bCode) {
		this.b2bCode = b2bCode;
	}
	/**
	 * B2B渠道编码
	 */
	public String getB2bCode() {
		return this.b2bCode;
	}
	/**
	 * B2C渠道编码
	 */
	public void setB2cCode(String b2cCode) {
		this.b2cCode = b2cCode;
	}
	/**
	 * B2C渠道编码
	 */
	public String getB2cCode() {
		return this.b2cCode;
	}
	/**
	 * 是否支持B2B
	 */
	public void setIsB2b(Boolean isB2b) {
		this.isB2b = isB2b;
	}
	/**
	 * 是否支持B2B
	 */
	public Boolean getIsB2b() {
		return this.isB2b;
	}
	/**
	 * 是否支持B2C
	 */
	public void setIsB2c(Boolean isB2c) {
		this.isB2c = isB2c;
	}
	/**
	 * 是否支持B2C
	 */
	public Boolean getIsB2c() {
		return this.isB2c;
	}
	/**
	 * 是否支持代扣
	 */
	public void setIsCharge(Boolean isCharge) {
		this.isCharge = isCharge;
	}
	/**
	 * 是否支持代扣
	 */
	public Boolean getIsCharge() {
		return this.isCharge;
	}
	/**
	 * 是否支持代付
	 */
	public void setIsPay(Boolean isPay) {
		this.isPay = isPay;
	}
	/**
	 * 是否支持代付
	 */
	public Boolean getIsPay() {
		return this.isPay;
	}
	/**
	 * 是否支持鉴权
	 */
	public void setIsAuth(Boolean isAuth) {
		this.isAuth = isAuth;
	}
	/**
	 * 是否支持鉴权
	 */
	public Boolean getIsAuth() {
		return this.isAuth;
	}
	/**
	 * 是否支持快捷支付
	 */
	public void setIsFastpay(Boolean isFastpay) {
		this.isFastpay = isFastpay;
	}
	/**
	 * 是否支持快捷支付
	 */
	public Boolean getIsFastpay() {
		return this.isFastpay;
	}
	/**
	 * 备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 备注
	 */
	public String getRemark() {
		return this.remark;
	}

}
