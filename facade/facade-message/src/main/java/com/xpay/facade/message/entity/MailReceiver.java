/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.message.entity;

import com.xpay.common.statics.annotations.PK;

import java.io.Serializable;

/**
 * 邮件接收人
 */
public class MailReceiver implements Serializable {
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
	 * 分组名
	 */
	private String groupKey;
	/**
	 * 描述/备注
	 */
	private String remark;
	/**
	 * 发件人
	 */
	private String from;
	/**
	 * 收件人
	 */
	private String to;
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
	 * 分组名
	 */
	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}
	/**
	 * 分组名
	 */
	public String getGroupKey() {
		return this.groupKey;
	}
	/**
	 * 描述/备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 描述/备注
	 */
	public String getRemark() {
		return this.remark;
	}
	/**
	 * 发件人
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * 发件人
	 */
	public String getFrom() {
		return this.from;
	}
	/**
	 * 收件人
	 */
	public void setTo(String to) {
		this.to = to;
	}
	/**
	 * 收件人
	 */
	public String getTo() {
		return this.to;
	}

}
