package com.ld.blog.infra.repository.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.admin.infra.mapper.RouteMapper;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.entity.SysUserRole;
import com.ld.blog.domain.repository.RoleRepository;
import com.ld.blog.infra.mapper.SysRoleMapper;
import com.ld.blog.infra.mapper.SysUserRoleMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * (Route)资源库
 *
 * @author liudong
 * @since 2022-03-23 23:14:36
 */
@Component
public class RoleRepositoryImpl implements RoleRepository {
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private RouteMapper routeMapper;

    @Override
    public List<SysRole> getRoles(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        SysUserRole entity = new SysUserRole();
        entity.setSysUserId(userId);
        queryWrapper.setEntity(entity);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(sysUserRoles)) {
            return Collections.emptyList();
        } else {
            return roleMapper.selectBatchIds(sysUserRoles.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toSet()));
        }
    }


}
