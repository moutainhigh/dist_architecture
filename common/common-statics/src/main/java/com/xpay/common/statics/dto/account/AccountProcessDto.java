package com.xpay.common.statics.dto.account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccountProcessDto implements Serializable {
    private static final long serialVersionUID = -1636546596635244115L;

    /**
     * 交易发生时间
     */
    private Date trxTime = new Date();

    /**
     * 交易流水号
     */
    private String trxNo;

    /**
     * 商户交易流水号
     */
    private String mchTrxNo;

    /**
     * 账务处理类型
     */
    private Integer processType;

    /**
     * 需要处理的是的哪种账户金额
     */
    private Integer amountType;

    /**
     * 用户编号
     */
    private String userNo;

    /**
     * 账务处理金额
     */
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 手续费
     */
    private BigDecimal fee = BigDecimal.ZERO;

    /**
     * 业务类型
     */
    private Integer bussType;
    /**
     * 业务编码
     */
    private Integer bussCode = null;
    /**
     * 描述
     */
    private String desc;


    /**------------------------ 特定场景使用的属性 START ------------------------*/
    /**
     * 异步处理时的待账务处理表的数据唯一约束是否需要打破，如：同一笔订单，先后动用不同的金额来处理时可使用
     */
    private String pendingDeUniqueKey;
    /**
     * 记账时间(账务内部逻辑处理时使用)
     */
    private Date accountTime = null;
    /**
     * 账务扣款记录(账务内部逻辑处理时使用)
     */
    private AccountDetailDto accountDetailDto = null;
    /**------------------------ 特定场景使用的属性 END ------------------------*/

    public Date getTrxTime() {
        return trxTime;
    }

    public void setTrxTime(Date trxTime) {
        this.trxTime = trxTime;
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }

    public String getMchTrxNo() {
        return mchTrxNo;
    }

    public void setMchTrxNo(String mchTrxNo) {
        this.mchTrxNo = mchTrxNo;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public Integer getAmountType() {
        return amountType;
    }

    public void setAmountType(Integer amountType) {
        this.amountType = amountType;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getBussType() {
        return bussType;
    }

    public void setBussType(Integer bussType) {
        this.bussType = bussType;
    }

    public Integer getBussCode() {
        return bussCode;
    }

    public void setBussCode(Integer bussCode) {
        this.bussCode = bussCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPendingDeUniqueKey() {
        return pendingDeUniqueKey;
    }

    public void setPendingDeUniqueKey(String pendingDeUniqueKey) {
        this.pendingDeUniqueKey = pendingDeUniqueKey;
    }

    public Date getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(Date accountTime) {
        this.accountTime = accountTime;
    }

    public AccountDetailDto getAccountDetailDto() {
        return accountDetailDto;
    }

    public void setAccountDetailDto(AccountDetailDto accountDetailDto) {
        this.accountDetailDto = accountDetailDto;
    }
}
