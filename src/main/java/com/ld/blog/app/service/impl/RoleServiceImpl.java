package com.ld.blog.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.Route;
import com.ld.blog.admin.domain.repository.RouteRepository;
import com.ld.blog.admin.infra.mapper.RouteMapper;
import com.ld.blog.app.service.RoleService;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.entity.SysRolePermission;
import com.ld.blog.domain.repository.RoleRepository;
import com.ld.blog.infra.mapper.SysRoleMapper;
import com.ld.blog.infra.mapper.SysRolePermissionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * (Role)应用服务
 *
 * @author liudong
 * @since 2022-03-24 20:22:09
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;
    @Autowired
    private RouteMapper routeMapper;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Page<SysRole> selectList(Page<SysRole> pageRequest, SysRole role) {
        Page<SysRole> sysRolePage = roleMapper.selectPage(pageRequest, new QueryWrapper<>(role));
        for (SysRole sysRole : sysRolePage.getRecords()) {
            sysRole.setRoutes(getRoleWithRoutesByRoleId(sysRole.getId()));
        }
        return sysRolePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(List<SysRole> roles) {
        roles.forEach(role -> {
            if (role.getId() == null) {
                roleMapper.insert(role);
            } else {
                roleMapper.updateById(role);
            }
            saveRouteAuth(role.getRoutes(), role.getId());
        });
    }

    private void saveRouteAuth(List<Route> routes, Long roleId) {
        List<Route> flatRoutes = new ArrayList<>();
        flatRoutes(routes, flatRoutes);
        List<Route> allRouteList = routeMapper.selectRoleRoutes(roleId);
        // 删除的路由权限数据
        List<Route> deleteRoutes = allRouteList.stream().filter(r -> {
            for (Route route : flatRoutes) {
                if (Objects.equals(route.getId(), r.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (!deleteRoutes.isEmpty()) {
            for (Route deleteRoute : deleteRoutes) {
                if (deleteRoute.hasRole(roleId)) {
                    Route routeDb = routeMapper.selectById(deleteRoute.getId());
                    routeDb.setRoles(deleteRole(roleId, routeDb.getRoles()));
                    routeMapper.updateById(routeDb);
                }

            }
        }

        // 更新的路由权限数据
        for (Route route : flatRoutes) {
            Route routeDb = routeMapper.selectById(route.getId());
            Assert.notNull(routeDb, "数据不存在,请刷新页面");
            if (!routeDb.hasRole(roleId)) {
                routeDb.setRoles(addRole(roleId, routeDb.getRoles()));
                routeMapper.updateById(routeDb);
            }
        }
    }

    private void flatRoutes(List<Route> routes, List<Route> list) {
        if (CollectionUtils.isEmpty(routes)) {
            return;
        }
        for (Route route : routes) {
            list.add(route);
            flatRoutes(route.getChildren(), list);
        }
    }

    private String addRole(Long roleId, String roles) {
        return StringUtils.isBlank(roles) ? String.valueOf(roleId) : roles + "," + roleId;
    }

    private String deleteRole(Long roleId, String roles) {
        if (StringUtils.isBlank(roles)) {
            return null;
        }
        List<String> newRoles = Arrays.stream(roles.split(",")).collect(Collectors.toList());
        newRoles.remove(roleId.toString());
        return String.join(",", newRoles);

    }

    @Override
    //@Cacheable(value = "userRole",key = "#userId") 用户角色更新时删除缓存
    public boolean isAdmin(Long userId) {
        List<SysRole> roleList = roleRepository.getRoles(userId);
        if (CollectionUtils.isEmpty(roleList)) {
            return false;
        } else {
            for (SysRole role : roleList) {
                if (role.isAdmin()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<SysRole> getRolesWithRoutes(Long userId) {
        List<SysRole> roles = roleRepository.getRoles(userId);
        for (SysRole role : roles) {
            List<Route> routes = routeMapper.selectRoleRoutes(role.getId());
            role.setRoutes(routeRepository.buildRouteTree(routes));
        }
        return roles;
    }

    @Override
    public List<Route> getRoleWithRoutesByRoleId(Long roleId) {
        List<Route> routes = routeMapper.selectRoleRoutes(roleId);
        return routeRepository.buildRouteTree(routes);
    }

    @Override
    public SysRole selectById(Long id) {
        SysRole sysRole = roleMapper.selectById(id);
        List<Route> routes = routeMapper.selectRoleRoutes(id);
        sysRole.setRoutes(routeRepository.buildRouteTree(routes));
        return sysRole;
    }

    @Override
    public Page<SysRole> selectRolesHasPermission(Page<SysRole> pageRequest, SysPermission permission) {
        List<SysRolePermission> sysRolePermissions = rolePermissionMapper.selectList(new QueryWrapper<>(SysRolePermission.builder().sysPermissionId(permission.getId()).build()));
        List<Long> roleIds = sysRolePermissions.stream().map(SysRolePermission::getSysRoleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roleIds)) {
            return new Page<>();
        }
        return roleMapper.selectPage(pageRequest, new QueryWrapper<SysRole>().in("id", roleIds));
    }

    @Override
    public Page<SysRole> selectRolesWithoutPermission(Page<SysRole> pageRequest, SysPermission permission) {
        List<SysRolePermission> sysRolePermissions = rolePermissionMapper.selectList(new QueryWrapper<>(SysRolePermission.builder().sysPermissionId(permission.getId()).build()));
        List<Long> roleIds = sysRolePermissions.stream().map(SysRolePermission::getSysRoleId).distinct().collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return roleMapper.selectPage(pageRequest, new QueryWrapper<>());
        }
        return roleMapper.selectPage(pageRequest, new QueryWrapper<SysRole>().notIn("id", roleIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRolesPermission(SysPermission permission, List<SysRole> assignedRoles,
                                      List<SysRole> notAssignedRoles) {
        List<SysRolePermission> sysRolePermissions = rolePermissionMapper.selectList(new QueryWrapper<>(SysRolePermission.builder().sysPermissionId(permission.getId()).build()));
        if (CollectionUtils.isNotEmpty(sysRolePermissions)) {
            rolePermissionMapper.deleteBatchIds(sysRolePermissions.stream().map(SysRolePermission::getId).collect(Collectors.toList()));

        }

        if (CollectionUtils.isNotEmpty(assignedRoles)) {
            List<SysRolePermission> rolePermissions = assignedRoles.stream()
                    .map(role -> SysRolePermission.builder().sysPermissionId(permission.getId()).sysRoleId(role.getId()).build())
                    .collect(Collectors.toList());
            // todo 批量操作
            for (SysRolePermission rolePermission : rolePermissions) {
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }
}
