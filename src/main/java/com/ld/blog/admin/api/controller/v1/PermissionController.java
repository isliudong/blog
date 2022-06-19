package com.ld.blog.admin.api.controller.v1;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.app.service.PermissionService;
import com.ld.blog.core.security.permission.Permission;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.infra.mapper.PermissionMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * (Permission)表控制层
 *
 * @author liudong
 * @since 2022-06-11 21:55:39
 */

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "列表")
    @Permission
    @GetMapping
    public ResponseEntity<Page<SysPermission>> list(SysPermission permission, Page<SysPermission> pageRequest) {
        Page<SysPermission> list = permissionService.selectList(pageRequest, permission);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "明细")
    @Permission
    @GetMapping("/{id}")
    public ResponseEntity<SysPermission> detail(@PathVariable Long id) {
        SysPermission permission = permissionMapper.selectById(id);
        return ResponseEntity.ok(permission);
    }

    @ApiOperation(value = "创建")
    @Permission
    @PostMapping
    public ResponseEntity<List<SysPermission>> save(@RequestBody List<SysPermission> permissions) {
        permissionService.saveData(permissions);
        return ResponseEntity.ok(permissions);
    }

    @ApiOperation(value = "删除")
    @Permission
    @DeleteMapping
    public ResponseEntity<Integer> remove(@RequestBody SysPermission permission) {
        permissionMapper.deleteById(permission);
        return ResponseEntity.ok(1);
    }

}

