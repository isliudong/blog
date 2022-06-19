package com.ld.blog.api.controller.v1;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.api.controller.v1.dto.ArticleQueryParam;
import com.ld.blog.api.controller.v1.dto.UserQueryParam;
import com.ld.blog.app.service.ArticleService;
import com.ld.blog.app.service.UserService;
import com.ld.blog.core.security.core.UserContext;
import com.ld.blog.core.security.permission.Permission;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.domain.entity.SysUser;
import com.ld.blog.infra.mapper.SysUserMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liudong
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    SysUserMapper userMapper;
    UserService userService;
    UserContext userContext;
    ArticleService articleService;

    @Permission
    @GetMapping
    public ResponseEntity<Page<SysUser>> getList(Page<SysUser> page, UserQueryParam param) {
        if ("+id".equals(param.getSort())) {
            page.addOrder(OrderItem.asc("id"));
        } else {
            page.addOrder(OrderItem.desc("id"));
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(SysUser.builder().username(param.getUsername()).email(param.getEmail()).build());
        Page<SysUser> userPage = userMapper.selectPage(page, queryWrapper);
        for (SysUser record : userPage.getRecords()) {
            record.setPassword(null);
        }
        return ResponseEntity.ok(userPage);
    }

    @Permission(permissionLogin = true)
    @GetMapping("/current")
    public ResponseEntity<SysUser> getCurrent() {
        return ResponseEntity.ok(userContext.getCurrentUser());
    }

    @Permission
    @GetMapping("/{id}")
    public ResponseEntity<SysUser> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.selectById(id));
    }

    @Permission
    @GetMapping("/account/{username}")
    public ResponseEntity<SysUser> getList(@PathVariable String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysUser.FILED_USERNAME, username);
        return ResponseEntity.ok(userMapper.selectOne(queryWrapper));
    }

    @Permission(permissionPublic = true)
    @PostMapping("/register")
    public ResponseEntity<Integer> save(@RequestBody @Validated(SysUser.Register.class) SysUser user) {
        userService.createUser(user);
        return ResponseEntity.ok(1);
    }


    @Permission
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody SysUser user) {
        userService.createUser(user);
        return ResponseEntity.ok(1);
    }

    @Permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return ResponseEntity.ok(1);
    }

    @Permission(permissionLogin = true)
    @PutMapping
    public ResponseEntity<Integer> update(@RequestBody @Validated(SysUser.Update.class) SysUser user) {
        userContext.validUser(user.getId());
        return ResponseEntity.ok(userMapper.updateById(user));
    }

    @Permission(permissionLogin = true)
    @GetMapping("/articles")
    public ResponseEntity<Page<Article>> getList(Page<Article> page, ArticleQueryParam param) {
        return ResponseEntity.ok(articleService.selectList(page, param, userContext.getCurrentUser().getId()));
    }


}
