package com.ld.blog.infra.mapper;


import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.blog.domain.entity.SysPermission;

/**
 * @author liudong
 */
public interface PermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 批量更新角色权限
     *
     * @param permissions 权限列表
     */
    void batchUpdate(List<SysPermission> permissions);
}
