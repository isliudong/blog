package com.ld.blog.app.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.core.exception.CommonException;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.entity.SysUser;
import com.ld.blog.domain.entity.SysUserRole;
import com.ld.blog.infra.mapper.SysRoleMapper;
import com.ld.blog.infra.mapper.SysUserMapper;
import com.ld.blog.infra.mapper.SysUserRoleMapper;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liudong
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final SysUserRoleMapper sysUserRoleMapper;


    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public void createUser(SysUser user) {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(user.getUsername());
        SysUser dbUser = userMapper.selectOne(new QueryWrapper<>(sysUser));
        if (dbUser != null) {
            throw new CommonException("用户名已被使用,换一个试试吧");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        SysRole sysRole = new SysRole();
        sysRole.setName("user");
        SysRole userRole = roleMapper.selectOne(new QueryWrapper<>(sysRole));
        SysUserRole userRoleRel = new SysUserRole();
        userRoleRel.setSysUserId(user.getId());
        //初始化user角色
        if (userRole != null) {
            userRoleRel.setSysRoleId(userRole.getId());
        }
        sysUserRoleMapper.insert(userRoleRel);
    }

    public SysUser getUser(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysUser.FILED_USERNAME, username);
        List<SysUser> sysUsers = userMapper.selectList(queryWrapper);

        if (sysUsers.isEmpty()) {
            return null;
        } else if (sysUsers.size() == 1) {
            SysUser user = sysUsers.get(0);
            List<SysRole> sysRoles = getRoles(user.getId());
            user.setRoles(sysRoles);
            return user;
        } else {
            throw new CommonException("名称不唯一");
        }
    }

    public List<SysRole> getRoles(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        QueryWrapper<SysUserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq(SysUserRole.USER_ID, userId);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(userRoleQueryWrapper);
        if (sysUserRoles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> roles = sysUserRoles.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toList());
        QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.in(SysRole.ID, roles);
        return roleMapper.selectList(roleQueryWrapper);
    }

    public Boolean checkNameUsable(String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysUser.FILED_USERNAME, username);
        SysUser user = userMapper.selectOne(queryWrapper);
        return user == null;
    }
}
