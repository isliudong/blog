package com.ld.blog.admin.domain.repository;

import java.util.List;

import com.ld.blog.admin.api.controller.dto.RouteDTO;
import com.ld.blog.admin.domain.entity.Route;

/**
 * (Route)资源库
 *
 * @author liudong
 * @since 2022-03-23 23:14:37
 */
public interface RouteRepository {
    /**
     * 查询
     *
     * @param route 查询条件
     * @return 返回值
     */
    List<Route> selectList(Route route);

    /**
     * 根据主键查询（可关联表）
     *
     * @param id 主键
     * @return 返回值
     */
    Route selectByPrimary(Long id);

    void deleteByPrimaryKey(Route route);

    /**
     * 根据父id，构建树结构
     *
     * @return 路由树
     */
    List<Route> buildRouteTree(List<Route> routes);

    List<RouteDTO> createOrUpdate(List<RouteDTO> routes);
}
