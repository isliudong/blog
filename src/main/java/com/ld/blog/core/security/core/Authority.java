package com.ld.blog.core.security.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.entity.SysRolePermission;
import com.ld.blog.infra.mapper.PermissionMapper;
import com.ld.blog.infra.mapper.SysRoleMapper;
import com.ld.blog.infra.mapper.SysRolePermissionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * @author liudong
 */
@Component
public class Authority implements ApplicationRunner {

    public static List<String> publicPer = new ArrayList<>(Arrays.asList("post/login"));
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private Map<String, List<String>> authorities;


    public Authority(SysRoleMapper sysRoleMapper,
                     SysRolePermissionMapper sysRolePermissionMapper,
                     PermissionMapper permissionMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<SysRole> roles = sysRoleMapper.selectList(new QueryWrapper<>());
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(new QueryWrapper<>());
        // 未处理角色继承的权限
        authorities = roles.stream().collect(Collectors.toMap(SysRole::getName, e -> {
            List<Long> permissionIds = rolePermissions.stream()
                    .filter(p -> p.getSysRoleId().equals(e.getId()))
                    .map(SysRolePermission::getSysPermissionId)
                    .collect(Collectors.toList());

            List<SysPermission> permissions = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(permissionIds)) {
                permissions = permissionMapper.selectBatchIds(permissionIds);

            }
            QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
            //登录可用接口赋予每个角色
            queryWrapper.eq(SysPermission.FILED_LOGIN_ACCESS, true);
            List<SysPermission> permissions2 = permissionMapper.selectList(queryWrapper);
            permissions.addAll(permissions2);
            return permissions.stream().map(SysPermission::getUrl).collect(Collectors.toList());
        }));
    }

    public boolean hasAuthority(String role, String authority) {
        if (StringUtils.isBlank(role)) {
            return false;
        }
        List<String> roleAuthorities = authorities.get(role);
        if (CollectionUtils.isEmpty(roleAuthorities)) {
            return false;
        }
        for (String roleAuthority : roleAuthorities) {
            if (antPathMatcher.match(roleAuthority, authority)) {
                return true;
            }
        }
        return false;
    }
}
