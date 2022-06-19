package com.ld.blog.admin.api.controller.dto;

import java.util.List;

import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.domain.entity.SysRole;
import lombok.Data;

/**
 * The type Permission roles param.
 *
 * @author liudong 2022-06-12 15:04:39
 */
@Data
public class PermissionRolesParam {
    SysPermission permission;
    List<SysRole> assignedRoles;
    List<SysRole> notAssignedRoles;
}
