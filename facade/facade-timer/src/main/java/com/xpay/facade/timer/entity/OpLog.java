/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.timer.entity;

import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;

/**
 * 操作日志表
 */
public class OpLog implements Serializable {
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
	 * 操作人
	 */
	private String operator;
	/**
	 * 操作主体
	 */
	private String objKey;
	/**
	 * 内容描述
	 */
	private Object content;
	//columns END


	/**
	 * 自增主键
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 自增主键
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
	 * 操作人
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}
	/**
	 * 操作人
	 */
	public String getOperator() {
		return this.operator;
	}
	/**
	 * 操作主体
	 */
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	/**
	 * 操作主体
	 */
	public String getObjKey() {
		return this.objKey;
	}
	/**
	 * 内容描述
	 */
	public void setContent(Object content) {
		this.content = content;
	}
	/**
	 * 内容描述
	 */
	public Object getContent() {
		return this.content;
	}

}
