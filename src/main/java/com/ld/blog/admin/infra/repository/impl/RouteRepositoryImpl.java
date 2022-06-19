package com.ld.blog.admin.infra.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.admin.api.controller.dto.RouteDTO;
import com.ld.blog.admin.domain.entity.Route;
import com.ld.blog.admin.domain.repository.RouteRepository;
import com.ld.blog.admin.infra.mapper.RouteMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * (Route)资源库
 *
 * @author liudong
 * @since 2022-03-23 23:14:36
 */
@Component
public class RouteRepositoryImpl implements RouteRepository {
    @Resource
    private RouteMapper routeMapper;

    @Override
    public List<Route> selectList(Route route) {
        return routeMapper.selectList(new QueryWrapper<>(route));
    }

    @Override
    public Route selectByPrimary(Long id) {
        Route route = new Route();
        route.setId(id);
        List<Route> routes = routeMapper.selectList(new QueryWrapper<>(route));
        if (CollectionUtils.isEmpty(routes)) {
            return null;
        }
        return routes.get(0);
    }

    @Override
    public void deleteByPrimaryKey(Route route) {
        routeMapper.deleteById(route);
    }

    @Override
    public List<Route> buildRouteTree(List<Route> routes) {
        if (CollectionUtils.isEmpty(routes)) {
            return Collections.emptyList();
        }
        List<Route> parentRoutes = routes.stream().filter(route1 -> route1.getParentId() == null).collect(Collectors.toList());
        List<Route> childrenRoutes = routes.stream().filter(route1 -> route1.getParentId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(childrenRoutes)) {
            Map<Long, Long> map = new HashMap<>(childrenRoutes.size());
            parentRoutes.forEach(entity -> getChildren(childrenRoutes, entity, map));
        }
        return parentRoutes;
    }

    @Override
    public List<RouteDTO> createOrUpdate(List<RouteDTO> routes) {
        saveRoutes(routes);
        return routes;
    }

    @Async
    protected void saveRoutes(List<RouteDTO> routes) {
        for (RouteDTO route : routes) {
            Route route1 = new Route();
            BeanUtils.copyProperties(route, route1);
            if (route.getMeta() != null) {
                route1.setIcon(route.getMeta().getIcon());
                route1.setTitle(route.getMeta().getTitle());
                route1.setAffix(route.getMeta().getAffix());
                route1.setNoCache(route.getMeta().getNoCache());
            }

            // 原路由角色授权数据
            QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
            Route entity = new Route();
            entity.setPath(route.getPath());
            entity.setName(route.getName());
            queryWrapper.setEntity(entity);
            Route oldRoute = routeMapper.selectOne(queryWrapper);
            if (Objects.nonNull(oldRoute)) {
                route1.setRoles(oldRoute.getRoles());
                routeMapper.deleteById(oldRoute);
            }

            routeMapper.insert(route1);

            List<RouteDTO> children = route.getChildren();
            StringBuilder stringBuilder = new StringBuilder();

            if (CollectionUtils.isNotEmpty(children)) {
                for (RouteDTO child : children) {
                    stringBuilder.delete(0, stringBuilder.length());
                    child.setParentId(route1.getId());
                    if (StringUtils.isNotBlank(route1.getParentIdPath())) {
                        stringBuilder.append(route1.getParentIdPath());
                    }
                    String path = stringBuilder.append("/").append(route1.getId()).toString();
                    child.setParentIdPath(path);
                }
                saveRoutes(children);
            }
        }
    }

    private void getChildren(List<Route> childrenRoutes, Route route, Map<Long, Long> map) {
        List<Route> children = new ArrayList<>();
        childrenRoutes.stream()
                .filter(entity -> !map.containsKey(entity.getId()))
                .filter(entity -> entity.getParentId().equals(route.getId()))
                .forEach(entity -> {
                    map.put(entity.getId(), entity.getParentId());
                    getChildren(childrenRoutes, entity, map);
                    children.add(entity);
                });
        route.setChildren(children);
    }
}
