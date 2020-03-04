/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.config.entity;

import com.xpay.common.statics.annotations.PK;
import java.io.Serializable;

/**
 * 操作日志
 */
public class OperateLog implements Serializable {
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
	 * 操作类型(1=新增 2=修改 3=删除)
	 */
	private Integer opType;
	/**
	 * 操作主体编码
	 */
	private String objectCode;
	/**
	 * 操作主体Id
	 */
	private Long objectId;
	/**
	 * 备注
	 */
	private String remark;
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
	 * 操作类型(1=新增 2=修改 3=删除)
	 */
	public void setOpType(Integer opType) {
		this.opType = opType;
	}
	/**
	 * 操作类型(1=新增 2=修改 3=删除)
	 */
	public Integer getOpType() {
		return this.opType;
	}
	/**
	 * 操作主体编码
	 */
	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}
	/**
	 * 操作主体编码
	 */
	public String getObjectCode() {
		return this.objectCode;
	}
	/**
	 * 操作主体Id
	 */
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	/**
	 * 操作主体Id
	 */
	public Long getObjectId() {
		return this.objectId;
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
