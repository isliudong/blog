package com.ld.blog.core.security.core;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.app.service.RoleService;
import com.ld.blog.core.exception.CommonException;
import com.ld.blog.domain.entity.SysUser;
import com.ld.blog.domain.repository.RoleRepository;
import com.ld.blog.infra.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author liudong
 */
@Component
public class UserContext {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    public SysUser getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof UserDetails)) {
            //未登录
            return null;
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysUser.FILED_USERNAME, ((UserDetails) principal).getUsername());
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return null;
        }
        user.setRoles(roleService.getRolesWithRoutes(user.getId()));
        user.setPassword(null);
        return user;
    }

    public void validUser(Long userId) {
        if (userId == null) {
            throw new CommonException("无权操作");
        }
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new CommonException("无权操作");
        }

        if (currentUser.getId().equals(userId)) {
            return;
        }

        if (roleService.isAdmin(currentUser.getId())) {
            return;
        }

        throw new CommonException("无权操作");

    }
}
