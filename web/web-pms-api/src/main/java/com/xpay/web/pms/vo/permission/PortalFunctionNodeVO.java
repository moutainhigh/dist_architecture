package com.xpay.web.pms.vo.permission;

import java.util.List;

/**
 * 功能节点VO
 *
 * @author linguangsheng
 * @date 2019/11/1
 */
public class PortalFunctionNodeVO {

    /**
     * id
     */
    private Long id;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 功能名称
     */
    private String name;

    /**
     * 序号
     */
    private String number;

    /**
     * 权限标识
     */
    private String permissionFlag;

    /**
     * 功能类型
     */
    private Integer functionType;

    /**
     * url
     */
    private String url;

    private String targetName;

    /**
     * 子菜单项
     */
    private List<PortalFunctionNodeVO> children;

    /**
     * 子操作项
     */
    private List<PortalFunctionNodeVO> actionChildren;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public List<PortalFunctionNodeVO> getChildren() {
        return children;
    }

    public void setChildren(List<PortalFunctionNodeVO> children) {
        this.children = children;
    }

    public List<PortalFunctionNodeVO> getActionChildren() {
        return actionChildren;
    }

    public void setActionChildren(List<PortalFunctionNodeVO> actionChildren) {
        this.actionChildren = actionChildren;
    }
}
