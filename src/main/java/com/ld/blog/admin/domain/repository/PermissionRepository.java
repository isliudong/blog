package com.ld.blog.admin.domain.repository;


import java.util.List;

import com.ld.blog.domain.entity.SysPermission;

/**
 * (Permission)资源库
 *
 * @author liudong
 * @since 2022-06-11 21:55:40
 */
public interface PermissionRepository {
    /**
     * 查询
     *
     * @param permission 查询条件
     * @return 返回值
     */
    List<SysPermission> selectList(SysPermission permission);

    /**
     * 根据主键查询（可关联表）
     *
     * @param id 主键
     * @return 返回值
     */
    SysPermission selectByPrimary(Long id);
}
