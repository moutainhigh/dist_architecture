/*
 * Powered By [joinPay.com]
 */
package com.xpay.facade.merchant.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * 商户信息详情表
 */
public class MerchantDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    //columns START
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private java.util.Date createTime = new Date();

    /**
     * 版本号
     */
    private Long version = 0L;

    /**
     * 修改时间
     */
    private java.util.Date modifyTime;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 公司网址
     */
    private String url;

    /**
     * ICP证备案号
     */
    private String icp;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 传真号
     */
    private String fax;

    /**
     * IP段
     */
    private String ipSeg;

    /**
     * 经营面积
     */
    private Integer businessArea;

    /**
     * 员工数量
     */
    private Integer empNum;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 行业类别
     */
    private Integer industryType;

    /**
     * 公司成立日期
     */
    private java.util.Date establishDate;

    /**
     * 法人姓名
     */
    private String legalPerson;

    /**
     * 法人身份证号码
     */
    private String legalPersonId;

    /**
     * 法人身份证有效期
     */
    private java.util.Date legalPersonIdExpire;

    /**
     * 法人身份证地址
     */
    private String legalPersonIdAddress;

    /**
     * 法定代表人归属地。参考MerchantLocationEnum
     */
    private Integer legalPersonLocation;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 营业执照有效期
     */
    private java.util.Date licenseNoExpire;

    /**
     * 工商登记类型(个人，企业，个体,MerchantRegistryTypeEnum)
     */
    private Integer registryType;

    /**
     * 注册资本(万元)
     */
    private java.math.BigDecimal registerAmount;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 税务登记证(国税)
     */
    private String nationalTaxNo;

    /**
     * 税务登记证(地税)
     */
    private String landTaxNo;

    /**
     * 签约时间
     */
    private java.util.Date signTime;

    /**
     * 合同到期日
     */
    private java.util.Date contractExpire;

    /**
     * 业务联系人
     */
    private String busiContactName;

    /**
     * 业务联系手机号码
     */
    private String busiContactMobileNo;

    /**
     * 业务联系邮箱
     */
    private String busiContactEmail;

    /**
     * 业务联系联系QQ
     */
    private String busiContactQq;

    /**
     * 技术联系人
     */
    private String techContactName;

    /**
     * 技术联系手机号
     */
    private String techContactMobileNo;

    /**
     * 技术联系邮箱
     */
    private String techContactEmail;

    /**
     * 技术联系QQ
     */
    private String techContactQq;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 备注
     */
    private String remark;

    //columns END


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setModifyTime(java.util.Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public java.util.Date getModifyTime() {
        return this.modifyTime;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantNo() {
        return this.merchantNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setIcp(String icp) {
        this.icp = icp;
    }

    public String getIcp() {
        return this.icp;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return this.fax;
    }

    public void setIpSeg(String ipSeg) {
        this.ipSeg = ipSeg;
    }

    public String getIpSeg() {
        return this.ipSeg;
    }

    public void setBusinessArea(Integer businessArea) {
        this.businessArea = businessArea;
    }

    public Integer getBusinessArea() {
        return this.businessArea;
    }

    public void setEmpNum(Integer empNum) {
        this.empNum = empNum;
    }

    public Integer getEmpNum() {
        return this.empNum;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getBusinessScope() {
        return this.businessScope;
    }

    public void setIndustryType(Integer industryType) {
        this.industryType = industryType;
    }

    public Integer getIndustryType() {
        return this.industryType;
    }

    public void setEstablishDate(java.util.Date establishDate) {
        this.establishDate = establishDate;
    }

    public java.util.Date getEstablishDate() {
        return this.establishDate;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getLegalPerson() {
        return this.legalPerson;
    }

    public void setLegalPersonId(String legalPersonId) {
        this.legalPersonId = legalPersonId;
    }

    public String getLegalPersonId() {
        return this.legalPersonId;
    }

    public void setLegalPersonIdExpire(java.util.Date legalPersonIdExpire) {
        this.legalPersonIdExpire = legalPersonIdExpire;
    }

    public java.util.Date getLegalPersonIdExpire() {
        return this.legalPersonIdExpire;
    }

    public void setLegalPersonIdAddress(String legalPersonIdAddress) {
        this.legalPersonIdAddress = legalPersonIdAddress;
    }

    public String getLegalPersonIdAddress() {
        return this.legalPersonIdAddress;
    }

    public void setLegalPersonLocation(Integer legalPersonLocation) {
        this.legalPersonLocation = legalPersonLocation;
    }

    public Integer getLegalPersonLocation() {
        return this.legalPersonLocation;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getLicenseNo() {
        return this.licenseNo;
    }

    public void setLicenseNoExpire(java.util.Date licenseNoExpire) {
        this.licenseNoExpire = licenseNoExpire;
    }

    public java.util.Date getLicenseNoExpire() {
        return this.licenseNoExpire;
    }

    public void setRegistryType(Integer registryType) {
        this.registryType = registryType;
    }

    public Integer getRegistryType() {
        return this.registryType;
    }

    public void setRegisterAmount(java.math.BigDecimal registerAmount) {
        this.registerAmount = registerAmount;
    }

    public java.math.BigDecimal getRegisterAmount() {
        return this.registerAmount;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setNationalTaxNo(String nationalTaxNo) {
        this.nationalTaxNo = nationalTaxNo;
    }

    public String getNationalTaxNo() {
        return this.nationalTaxNo;
    }

    public void setLandTaxNo(String landTaxNo) {
        this.landTaxNo = landTaxNo;
    }

    public String getLandTaxNo() {
        return this.landTaxNo;
    }

    public void setSignTime(java.util.Date signTime) {
        this.signTime = signTime;
    }

    public java.util.Date getSignTime() {
        return this.signTime;
    }

    public void setContractExpire(java.util.Date contractExpire) {
        this.contractExpire = contractExpire;
    }

    public java.util.Date getContractExpire() {
        return this.contractExpire;
    }

    public void setBusiContactName(String busiContactName) {
        this.busiContactName = busiContactName;
    }

    public String getBusiContactName() {
        return this.busiContactName;
    }

    public void setBusiContactMobileNo(String busiContactMobileNo) {
        this.busiContactMobileNo = busiContactMobileNo;
    }

    public String getBusiContactMobileNo() {
        return this.busiContactMobileNo;
    }

    public void setBusiContactEmail(String busiContactEmail) {
        this.busiContactEmail = busiContactEmail;
    }

    public String getBusiContactEmail() {
        return this.busiContactEmail;
    }

    public void setBusiContactQq(String busiContactQq) {
        this.busiContactQq = busiContactQq;
    }

    public String getBusiContactQq() {
        return this.busiContactQq;
    }

    public void setTechContactName(String techContactName) {
        this.techContactName = techContactName;
    }

    public String getTechContactName() {
        return this.techContactName;
    }

    public void setTechContactMobileNo(String techContactMobileNo) {
        this.techContactMobileNo = techContactMobileNo;
    }

    public String getTechContactMobileNo() {
        return this.techContactMobileNo;
    }

    public void setTechContactEmail(String techContactEmail) {
        this.techContactEmail = techContactEmail;
    }

    public String getTechContactEmail() {
        return this.techContactEmail;
    }

    public void setTechContactQq(String techContactQq) {
        this.techContactQq = techContactQq;
    }

    public String getTechContactQq() {
        return this.techContactQq;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return this.isDelete;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

}
