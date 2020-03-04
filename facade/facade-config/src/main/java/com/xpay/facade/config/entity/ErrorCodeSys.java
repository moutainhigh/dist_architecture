/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import com.xpay.common.statics.annotations.PK;
import java.io.Serializable;

/**
 * 系统错误码表
 */
public class ErrorCodeSys implements Serializable {
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
	 * 错误码
	 */
	private String sysErrorCode;
	/**
	 * 错误码描述
	 */
	private String sysErrorDesc;
	/**
	 * 解决方案
	 */
	private String solveProject;
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
	 * 错误码
	 */
	public void setSysErrorCode(String sysErrorCode) {
		this.sysErrorCode = sysErrorCode;
	}
	/**
	 * 错误码
	 */
	public String getSysErrorCode() {
		return this.sysErrorCode;
	}
	/**
	 * 错误码描述
	 */
	public void setSysErrorDesc(String sysErrorDesc) {
		this.sysErrorDesc = sysErrorDesc;
	}
	/**
	 * 错误码描述
	 */
	public String getSysErrorDesc() {
		return this.sysErrorDesc;
	}
	/**
	 * 解决方案
	 */
	public void setSolveProject(String solveProject) {
		this.solveProject = solveProject;
	}
	/**
	 * 解决方案
	 */
	public String getSolveProject() {
		return this.solveProject;
	}

}
