/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.sequence.entity;

import java.io.Serializable;

/**
 * ID序列号表
 */
public class Sequence implements Serializable {

	private static final long serialVersionUID = 1L;

	//columns START
	/**
	 * 业务key(主键)
	 */
	private java.lang.String bussKey;

	/**
	 * 当前ID的起点
	 */
	private java.lang.Long currId;

	/**
	 * 下一个ID的起点
	 */
	private java.lang.Long nextId;

	/**
	 * 最后更新时间
	 */
	private java.util.Date modifyTime;

	//columns END


	/**
	 * 业务key(主键)
	 */
	public void setBussKey(java.lang.String bussKey) {
		this.bussKey = bussKey;
	}
	/**
	 * 业务key(主键)
	 */
	public java.lang.String getBussKey() {
		return this.bussKey;
	}
	/**
	 * 当前ID的起点
	 */
	public void setCurrId(java.lang.Long currId) {
		this.currId = currId;
	}
	/**
	 * 当前ID的起点
	 */
	public java.lang.Long getCurrId() {
		return this.currId;
	}
	/**
	 * 下一个ID的起点
	 */
	public void setNextId(java.lang.Long nextId) {
		this.nextId = nextId;
	}
	/**
	 * 下一个ID的起点
	 */
	public java.lang.Long getNextId() {
		return this.nextId;
	}
	/**
	 * 最后更新时间
	 */
	public void setModifyTime(java.util.Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	/**
	 * 最后更新时间
	 */
	public java.util.Date getModifyTime() {
		return this.modifyTime;
	}

}
