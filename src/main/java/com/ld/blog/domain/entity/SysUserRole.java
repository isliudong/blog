package com.ld.blog.domain.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sys_user_role
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserRole extends AuditDomain implements Serializable {
    public static final String USER_ID = "sys_user_id";
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long sysUserId;
    private Long sysRoleId;
}
