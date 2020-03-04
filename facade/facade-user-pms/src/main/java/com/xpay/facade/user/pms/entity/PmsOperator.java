package com.xpay.facade.user.pms.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: Cmf
 * Date: 2019/10/9
 * Time: 18:09
 * Description: 操作员实体
 */
public class PmsOperator implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer version = 0;
    private Date createTime = new Date();
    private String loginName;// 登录名
    private String loginPwd; // 登录密码
    private String remark; // 描述
    private String realName; // 姓名
    private String mobileNo; // 手机号
    private Integer status; // 状态 PmsOperatorStatusEnum
    private Integer type; // 操作员类型 PmsOperatorTypeEnum（1:超级管理员，2:普通操作员），超级管理员由系统初始化时添加，不能删除


    private Date currLoginTime;// 当前登录时间
    private Date lastLoginTime;// 最后登录时间
    private Integer isChangedPwd;// 是否更改过密码         //todo 待删除
    private Integer pwdErrorCount; // 连续输错密码次数（连续5次输错就冻结帐号）
    private Date pwdErrorTime; // 最后输错密码的时间
    private Integer isLoginSmsVerify; //登录是否短信检验：100-是，101-否
    private Date validDate; //口令有效期
    private Date lastModPwdTime;// 最后一次修改密码时间
    private String creator; // 创建人
    private String updator;// 修改者，根据该字段进行判断审核时不能是同一个操作员
    private String historyPwd;// 历史密码（包括当前密码）

    //region 非数据库字段
    /**
     * 拥有的角色列表，非数据库字段
     */
    private List<Long> roleIds;
    //endregion


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCurrLoginTime() {
        return currLoginTime;
    }

    public void setCurrLoginTime(Date currLoginTime) {
        this.currLoginTime = currLoginTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getIsChangedPwd() {
        return isChangedPwd;
    }

    public void setIsChangedPwd(Integer isChangedPwd) {
        this.isChangedPwd = isChangedPwd;
    }

    public Integer getPwdErrorCount() {
        return pwdErrorCount;
    }

    public void setPwdErrorCount(Integer pwdErrorCount) {
        this.pwdErrorCount = pwdErrorCount;
    }

    public Date getPwdErrorTime() {
        return pwdErrorTime;
    }

    public void setPwdErrorTime(Date pwdErrorTime) {
        this.pwdErrorTime = pwdErrorTime;
    }

    public Integer getIsLoginSmsVerify() {
        return isLoginSmsVerify;
    }

    public void setIsLoginSmsVerify(Integer isLoginSmsVerify) {
        this.isLoginSmsVerify = isLoginSmsVerify;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public Date getLastModPwdTime() {
        return lastModPwdTime;
    }

    public void setLastModPwdTime(Date lastModPwdTime) {
        this.lastModPwdTime = lastModPwdTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getHistoryPwd() {
        return historyPwd;
    }

    public void setHistoryPwd(String historyPwd) {
        this.historyPwd = historyPwd;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
