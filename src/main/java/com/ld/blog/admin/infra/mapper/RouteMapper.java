package com.ld.blog.admin.infra.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.blog.admin.domain.entity.Route;

/**
 * (Route)应用服务
 *
 * @author liudong
 * @since 2022-03-23 23:14:36
 */
public interface RouteMapper extends BaseMapper<Route> {

    List<Route> selectRoleRoutes(Long roleId);
}

