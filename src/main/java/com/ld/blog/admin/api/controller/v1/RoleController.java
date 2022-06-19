package com.ld.blog.admin.api.controller.v1;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.api.controller.dto.PermissionRolesParam;
import com.ld.blog.app.service.RoleService;
import com.ld.blog.core.security.permission.Permission;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.repository.RoleRepository;
import com.ld.blog.infra.mapper.SysRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (Role)表控制层
 *
 * @author liudong
 * @since 2022-03-24 20:22:09
 */

@RestController("roleSiteController.v1")
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Permission
    public ResponseEntity<Page<SysRole>> list(SysRole role, Page<SysRole> pageRequest) {
        Page<SysRole> list = roleService.selectList(pageRequest, role);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/has-permission")
    @Permission
    public ResponseEntity<Page<SysRole>> selectRolesHasPermission(SysPermission permission, Page<SysRole> pageRequest) {
        Page<SysRole> list = roleService.selectRolesHasPermission(pageRequest, permission);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/without-permission")
    @Permission
    public ResponseEntity<Page<SysRole>> selectRolesWithoutPermission(SysPermission permission, Page<SysRole> pageRequest) {
        Page<SysRole> list = roleService.selectRolesWithoutPermission(pageRequest, permission);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/permission")
    @Permission
    public ResponseEntity<Integer> updateRolesPermission(@RequestBody PermissionRolesParam param) {
        roleService.updateRolesPermission(param.getPermission(),
                param.getAssignedRoles(), param.getNotAssignedRoles());
        return ResponseEntity.ok(1);
    }

    @GetMapping("/{id}")
    @Permission
    public ResponseEntity<SysRole> detail(@PathVariable Long id) {
        SysRole role = roleService.selectById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @Permission
    public ResponseEntity<List<SysRole>> save(@RequestBody List<SysRole> roles) {
        roleService.saveData(roles);
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping
    @Permission
    public ResponseEntity<Integer> remove(@RequestBody SysRole role) {
        roleMapper.deleteById(role);
        return ResponseEntity.ok(1);
    }

}

