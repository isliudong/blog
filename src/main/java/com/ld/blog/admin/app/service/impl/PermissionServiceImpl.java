package com.ld.blog.admin.app.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.app.service.PermissionService;
import com.ld.blog.admin.domain.repository.PermissionRepository;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.infra.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * (Permission)应用服务
 *
 * @author liudong
 * @since 2022-06-11 21:55:39
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Page<SysPermission> selectList(Page<SysPermission> pageRequest, SysPermission permission) {
        return permissionMapper.selectPage(pageRequest, new QueryWrapper<>(permission));
    }

    @Override
    public void saveData(List<SysPermission> permissions) {
        permissions.forEach(item -> {
            if (item.getId() == null) {
                permissionMapper.insert(item);
            } else {
                permissionMapper.updateById(item);
            }
        });
    }
}
