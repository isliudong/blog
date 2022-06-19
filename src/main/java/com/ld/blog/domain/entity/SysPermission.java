package com.ld.blog.domain.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sys_permission
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysPermission extends AuditDomain implements Serializable {
    public static final String FILED_LOGIN_ACCESS = "login_access";
    public static final String FILED_PUBLIC_ACCESS = "public_access";
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源类型：menu,button,
     */
    private String type;
    /**
     * 访问url地址
     */
    private String url;
    /**
     * 权限代码字符串
     */
    private String percode;
    /**
     * 是否登录可用
     */
    private Boolean loginAccess;
    /**
     * 是否公开
     */
    private Boolean publicAccess;
    /**
     * 父结点id
     */
    private Long parentid;
    /**
     * 父结点id列表串
     */
    private String parentids;
    /**
     * 排序号
     */
    private String sortstring;
    /**
     * 是否可用,1：可用，0不可用
     */
    private Boolean available;
}
