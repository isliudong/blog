package com.ld.blog.admin.infra.repository.impl;

import java.util.List;
import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.admin.domain.repository.PermissionRepository;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.infra.mapper.PermissionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * (Permission)资源库
 *
 * @author liudong
 * @since 2022-06-11 21:55:39
 */
@Component
public class PermissionRepositoryImpl implements PermissionRepository {
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<SysPermission> selectList(SysPermission permission) {
        return permissionMapper.selectList(new QueryWrapper<>(permission));
    }

    @Override
    public SysPermission selectByPrimary(Long id) {
        SysPermission permission = new SysPermission();
        permission.setId(id);
        List<SysPermission> permissions = permissionMapper.selectList(new QueryWrapper<>(permission));
        if (CollectionUtils.isEmpty(permissions)) {
            return null;
        }
        return permissions.get(0);
    }

}
