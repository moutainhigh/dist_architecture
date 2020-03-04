/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import com.xpay.common.statics.annotations.PK;
import java.io.Serializable;

/**
 * 通道错误码表
 */
public class ErrorCodeChannel implements Serializable {
	private static final long serialVersionUID = 1L;

	//columns START
	/**
	 * id
	 */
	@PK
	private Long id;
	/**
	 * 创建时间
	 */
	private java.util.Date createTime;
	/**
	 * 版本
	 */
	private Integer version;
	/**
	 * 通道编号
	 */
	private String channelNo;
	/**
	 * 通道响应码
	 */
	private String channelResponse;
	/**
	 * 通道响应码描述
	 */
	private String channelResponseDesc;
	/**
	 * 系统错误码
	 */
	private String sysErrorCode;
	/**
	 * 系统错误码描述
	 */
	private String sysErrorDesc;
	/**
	 * 处理方式
	 */
	private Integer solveWay;
	/**
	 * 业务类型
	 */
	private Integer bizType;
	//columns END


	/**
	 * id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * id
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
	 * 版本
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * 版本
	 */
	public Integer getVersion() {
		return this.version;
	}
	/**
	 * 通道编号
	 */
	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}
	/**
	 * 通道编号
	 */
	public String getChannelNo() {
		return this.channelNo;
	}
	/**
	 * 通道响应码
	 */
	public void setChannelResponse(String channelResponse) {
		this.channelResponse = channelResponse;
	}
	/**
	 * 通道响应码
	 */
	public String getChannelResponse() {
		return this.channelResponse;
	}
	/**
	 * 通道响应码描述
	 */
	public void setChannelResponseDesc(String channelResponseDesc) {
		this.channelResponseDesc = channelResponseDesc;
	}
	/**
	 * 通道响应码描述
	 */
	public String getChannelResponseDesc() {
		return this.channelResponseDesc;
	}
	/**
	 * 系统错误码
	 */
	public void setSysErrorCode(String sysErrorCode) {
		this.sysErrorCode = sysErrorCode;
	}
	/**
	 * 系统错误码
	 */
	public String getSysErrorCode() {
		return this.sysErrorCode;
	}
	/**
	 * 系统错误码描述
	 */
	public void setSysErrorDesc(String sysErrorDesc) {
		this.sysErrorDesc = sysErrorDesc;
	}
	/**
	 * 系统错误码描述
	 */
	public String getSysErrorDesc() {
		return this.sysErrorDesc;
	}
	/**
	 * 处理方式
	 */
	public void setSolveWay(Integer solveWay) {
		this.solveWay = solveWay;
	}
	/**
	 * 处理方式
	 */
	public Integer getSolveWay() {
		return this.solveWay;
	}
	/**
	 * 业务类型
	 */
	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}
	/**
	 * 业务类型
	 */
	public Integer getBizType() {
		return this.bizType;
	}

}
