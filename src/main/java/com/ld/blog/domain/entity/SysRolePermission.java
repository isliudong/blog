package com.ld.blog.domain.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sys_role_permission
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysRolePermission extends AuditDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    /**
     * 角色id
     */
    private Long sysRoleId;
    /**
     * 权限id
     */
    private Long sysPermissionId;
}
