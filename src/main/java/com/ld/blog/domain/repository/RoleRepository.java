package com.ld.blog.domain.repository;

import java.util.List;

import com.ld.blog.domain.entity.SysRole;

/**
 * (Route)资源库
 *
 * @author liudong
 * @since 2022-03-23 23:14:37
 */
public interface RoleRepository {

    /**
     * 获取用户角色
     *
     * @param userId 用户id
     * @return 用户角色
     */
    List<SysRole> getRoles(Long userId);


}
