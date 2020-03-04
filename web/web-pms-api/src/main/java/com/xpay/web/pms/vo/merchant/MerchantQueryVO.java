package com.xpay.web.pms.vo.merchant;

import java.io.Serializable;
import java.util.Date;


/**
 * Author: Cmf
 * Date: 2020.2.14
 * Time: 15:03
 * Description:
 */
public class MerchantQueryVO implements Serializable {
    private static final long serialVersionUID = 7919155227046962924L;
    private String merchantNo;      //商户编号
    private String fullName;        //商户全称
    private String shortName;       //商户简称
    private Integer status; //商户状态 com.xpay.common.statics.enums.merchant.MerchantStatusEnum
    private Integer merchantLevel;  //商户等级 com.xpay.common.statics.enums.merchant.MerchantLevelEnum
    private Date createTimeBegin;   //创建时间-开始
    private Date createTimeEnd;     //创建时间-结束

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMerchantLevel() {
        return merchantLevel;
    }

    public void setMerchantLevel(Integer merchantLevel) {
        this.merchantLevel = merchantLevel;
    }

    public Date getCreateTimeBegin() {
        return createTimeBegin;
    }

    public void setCreateTimeBegin(Date createTimeBegin) {
        this.createTimeBegin = createTimeBegin;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}
