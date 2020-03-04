/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import com.xpay.common.statics.annotations.PK;
import java.io.Serializable;

/**
 * 银行信息
 */
public class BankInfo implements Serializable {
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
	 * 银行行号(联行号)
	 */
	private String bankChannelNo;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 类别：1-清算行，2-开户行
	 */
	private Integer bankType;
	/**
	 * 清算行行号
	 */
	private String clearBankChannelNo;
	/**
	 * 总行简称(如: ABC、ICBC)
	 */
	private String headOfficeShortName;
	/**
	 * 总行全称(如: 中国农业银行)
	 */
	private String headOfficeFullName;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 地区编号
	 */
	private Integer cityCode;
	/**
	 * 是否省内：100=是，101=否
	 */
	private Integer isInProvince;
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
	 * 银行行号(联行号)
	 */
	public void setBankChannelNo(String bankChannelNo) {
		this.bankChannelNo = bankChannelNo;
	}
	/**
	 * 银行行号(联行号)
	 */
	public String getBankChannelNo() {
		return this.bankChannelNo;
	}
	/**
	 * 银行名称
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * 银行名称
	 */
	public String getBankName() {
		return this.bankName;
	}
	/**
	 * 类别：1-清算行，2-开户行
	 */
	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}
	/**
	 * 类别：1-清算行，2-开户行
	 */
	public Integer getBankType() {
		return this.bankType;
	}
	/**
	 * 清算行行号
	 */
	public void setClearBankChannelNo(String clearBankChannelNo) {
		this.clearBankChannelNo = clearBankChannelNo;
	}
	/**
	 * 清算行行号
	 */
	public String getClearBankChannelNo() {
		return this.clearBankChannelNo;
	}
	/**
	 * 总行简称(如: ABC、ICBC)
	 */
	public void setHeadOfficeShortName(String headOfficeShortName) {
		this.headOfficeShortName = headOfficeShortName;
	}
	/**
	 * 总行简称(如: ABC、ICBC)
	 */
	public String getHeadOfficeShortName() {
		return this.headOfficeShortName;
	}
	/**
	 * 总行全称(如: 中国农业银行)
	 */
	public void setHeadOfficeFullName(String headOfficeFullName) {
		this.headOfficeFullName = headOfficeFullName;
	}
	/**
	 * 总行全称(如: 中国农业银行)
	 */
	public String getHeadOfficeFullName() {
		return this.headOfficeFullName;
	}
	/**
	 * 省份
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * 省份
	 */
	public String getProvince() {
		return this.province;
	}
	/**
	 * 城市
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * 城市
	 */
	public String getCity() {
		return this.city;
	}
	/**
	 * 地区编号
	 */
	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}
	/**
	 * 地区编号
	 */
	public Integer getCityCode() {
		return this.cityCode;
	}
	/**
	 * 是否省内：100=是，101=否
	 */
	public void setIsInProvince(Integer isInProvince) {
		this.isInProvince = isInProvince;
	}
	/**
	 * 是否省内：100=是，101=否
	 */
	public Integer getIsInProvince() {
		return this.isInProvince;
	}

}
