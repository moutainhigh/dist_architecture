package com.xpay.facade.user.pms.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Cmf
 * Date: 2019/10/22
 * Time: 19:42
 * Description: 功能表实体，包括menu和action
 */
public class PmsFunction implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int MENU_TYPE = 1;
    public static final int ACTION_TYPE = 2;

    private Long id;
    private Integer version = 0;
    private Date createTime = new Date();
    private String name;                    // 名称NAME
    private String number;                  // 编号NUMBER
    private Long parentId;                  //父节点id
    private String permissionFlag;          //权限标识
    private Integer functionType;           //MENU_TYPE ,ACTION_TYPE


    private String url;                     //  后端API地址
    private String targetName;              // TARGETNAME; //用于刷新页面的配置


    //region 临时使用，非数据库字段
    private PmsFunction parent; // 父级ID
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPermissionFlag() {
        return permissionFlag;
    }

    public void setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public PmsFunction getParent() {
        return parent;
    }

    public void setParent(PmsFunction parent) {
        this.parent = parent;
    }
}
