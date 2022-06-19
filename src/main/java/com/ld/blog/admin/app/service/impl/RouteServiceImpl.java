package com.ld.blog.admin.app.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.app.service.RouteService;
import com.ld.blog.admin.domain.entity.Route;
import com.ld.blog.admin.domain.repository.RouteRepository;
import com.ld.blog.admin.infra.mapper.RouteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (Route)应用服务
 *
 * @author liudong
 * @since 2022-03-23 23:14:36
 */
@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private RouteMapper routeMapper;

    @Override
    public Page<Route> selectList(Page<Route> pageRequest, Route route) {
        return routeMapper.selectPage(pageRequest, new QueryWrapper<>(route));
    }

    @Override
    public void saveData(List<Route> routes) {
        routes.forEach(item -> {
            if (item.getId() == null) {
                routeMapper.insert(item);
            } else {
                routeMapper.updateById(item);
            }
        });
    }

    @Override
    public List<Route> selectList(Route route) {
        List<Route> routes = routeRepository.selectList(route);
        for (Route route1 : routes) {
            route1.setKey(route1.getId());
            route1.setMeta(Route.Mata.builder()
                    .title(route1.getTitle())
                    .icon(route1.getIcon())
                    .noCache(route1.getNoCache())
                    .affix(route1.getAffix())
                    .roles(route1.getRoles() == null ? null : new ArrayList<>(Arrays.asList(route1.getRoles().split(","))))
                    .build());
        }
        return routeRepository.buildRouteTree(routes);
    }


}
