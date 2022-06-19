package com.ld.blog.app.service;


import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.Route;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.domain.entity.SysRole;

/**
 * (Role)应用服务
 *
 * @author liudong
 * @since 2022-03-24 20:22:09
 */
public interface RoleService {

    /**
     * 查询数据
     *
     * @param pageRequest 分页参数
     * @param roles       查询条件
     * @return 返回值
     */
    Page<SysRole> selectList(Page<SysRole> pageRequest, SysRole roles);

    /**
     * 保存数据
     *
     * @param roles 数据
     */
    void saveData(List<SysRole> roles);

    boolean isAdmin(Long userId);

    List<SysRole> getRolesWithRoutes(Long userId);

    List<Route> getRoleWithRoutesByRoleId(Long roleId);

    SysRole selectById(Long id);


    /**
     * 拥有该权限的角色
     *
     * @param pageRequest the page request
     * @param permission  the permission
     * @return the page
     */
    Page<SysRole> selectRolesHasPermission(Page<SysRole> pageRequest, SysPermission permission);

    /**
     * 无该权限的角色
     *
     * @param pageRequest the page request
     * @param permission  the permission
     * @return the page
     */
    Page<SysRole> selectRolesWithoutPermission(Page<SysRole> pageRequest, SysPermission permission);

    /**
     * 更新接口角色权限
     *
     * @param permission       the permission
     * @param assignedRoles    the assigned roles
     * @param notAssignedRoles the not assigned roles
     */
    void updateRolesPermission(SysPermission permission, List<SysRole> assignedRoles, List<SysRole> notAssignedRoles);
}

