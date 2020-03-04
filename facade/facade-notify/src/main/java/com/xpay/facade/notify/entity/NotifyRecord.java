/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.notify.entity;


import java.io.Serializable;
import java.util.List;

/**
 * 业务通知记录表
 */
public class NotifyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * ID
     */
    private Long id;

    /**
     * VERSION
     */
    private Integer version;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    /**
     * 最后一次通知时间
     */
    private java.util.Date lastNotifyTime;

    /**
     * 消息topic
     */
    private String msgTopic;

    /**
     * 消息tag
     */
    private String msgTag;

    /**
     * 消息事件类型
     */
    private Integer msgEventType;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 业务流水号
     */
    private String trxNo;

    /**
     * 二级业务流水号
     */
    private String subTrxNo;

    /**
     * 三级业务流水号
     */
    private String thirdTrxNo;

    /**
     * 通知状态(1:成功:-1:未成功,默认-1)
     */
    private Integer status;

    /**
     * 通知内容
     */
    private String content;

    /**
     * extraInfo
     */
    private Object extraInfo;

    //columns END


    //region 非数据库字段
    private List<NotifyResponse> notifyResponseList;
    //endregion


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

    public void setLastNotifyTime(java.util.Date lastNotifyTime) {
        this.lastNotifyTime = lastNotifyTime;
    }

    public java.util.Date getLastNotifyTime() {
        return this.lastNotifyTime;
    }

    public void setMsgTopic(String msgTopic) {
        this.msgTopic = msgTopic;
    }

    public String getMsgTopic() {
        return this.msgTopic;
    }

    public void setMsgTag(String msgTag) {
        this.msgTag = msgTag;
    }

    public String getMsgTag() {
        return this.msgTag;
    }

    public void setMsgEventType(Integer msgEventType) {
        this.msgEventType = msgEventType;
    }

    public Integer getMsgEventType() {
        return this.msgEventType;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantNo() {
        return this.merchantNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public String getTrxNo() {
        return this.trxNo;
    }

    public void setSubTrxNo(String subTrxNo) {
        this.subTrxNo = subTrxNo;
    }

    public String getSubTrxNo() {
        return this.subTrxNo;
    }

    public void setThirdTrxNo(String thirdTrxNo) {
        this.thirdTrxNo = thirdTrxNo;
    }

    public String getThirdTrxNo() {
        return this.thirdTrxNo;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setExtraInfo(Object extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Object getExtraInfo() {
        return this.extraInfo;
    }

    public List<NotifyResponse> getNotifyResponseList() {
        return notifyResponseList;
    }

    public void setNotifyResponseList(List<NotifyResponse> notifyResponseList) {
        this.notifyResponseList = notifyResponseList;
    }
}
