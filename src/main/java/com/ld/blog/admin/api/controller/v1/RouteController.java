package com.ld.blog.admin.api.controller.v1;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.api.controller.dto.RouteDTO;
import com.ld.blog.admin.app.service.RouteService;
import com.ld.blog.admin.domain.entity.Route;
import com.ld.blog.admin.domain.repository.RouteRepository;
import com.ld.blog.core.security.permission.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (Route)表控制层
 *
 * @author liudong
 * @since 2022-03-23 23:14:36
 */

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteService routeService;

    @Permission(permissionPublic = true)
    @GetMapping("/page")
    public ResponseEntity<Page<Route>> list(Route route, Page<Route> pageRequest) {
        Page<Route> list = routeService.selectList(pageRequest, route);
        return ResponseEntity.ok(list);
    }

    @Permission(permissionPublic = true)
    @GetMapping("/list")
    public ResponseEntity<List<Route>> listAll(Route route) {
        List<Route> list = routeService.selectList(route);
        return ResponseEntity.ok(list);
    }

    @Permission
    @PutMapping("/list")
    public ResponseEntity<List<RouteDTO>> updateRoutes(@RequestBody List<RouteDTO> routes) {
        List<RouteDTO> route = routeRepository.createOrUpdate(routes);
        return ResponseEntity.ok(route);
    }

    @Permission
    @PostMapping
    public ResponseEntity<List<Route>> save(@RequestBody List<Route> routes) {
        routeService.saveData(routes);
        return ResponseEntity.ok(routes);
    }

    @Permission
    @DeleteMapping
    public ResponseEntity<Integer> remove(@RequestBody Route route) {
        routeRepository.deleteByPrimaryKey(route);
        return ResponseEntity.ok(1);
    }

}

