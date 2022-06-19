package com.ld.blog.admin.app.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.Route;

/**
 * (Route)应用服务
 *
 * @author liudong
 * @since 2022-03-23 23:14:36
 */
public interface RouteService {

    /**
     * 查询数据
     *
     * @param pageRequest 分页参数
     * @param routes      查询条件
     * @return 返回值
     */
    Page<Route> selectList(Page<Route> pageRequest, Route routes);

    /**
     * 保存数据
     *
     * @param routes 数据
     */
    void saveData(List<Route> routes);

    List<Route> selectList(Route route);
}

