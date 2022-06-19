package com.ld.blog.admin.app.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.domain.entity.SysPermission;

/**
 * (Permission)应用服务
 *
 * @author liudong
 * @since 2022-06-11 21:55:39
 */
public interface PermissionService {

    /**
     * 查询数据
     *
     * @param pageRequest 分页参数
     * @param permissions 查询条件
     * @return 返回值
     */
    Page<SysPermission> selectList(Page<SysPermission> pageRequest, SysPermission permissions);

    /**
     * 保存数据
     *
     * @param permissions 数据
     */
    void saveData(List<SysPermission> permissions);

}

