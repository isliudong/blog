package com.ld.blog.core.security.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.entity.SysUser;
import com.ld.blog.domain.entity.SysUserRole;
import com.ld.blog.infra.mapper.SysRoleMapper;
import com.ld.blog.infra.mapper.SysUserMapper;
import com.ld.blog.infra.mapper.SysUserRoleMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * UserDetailsService的实现,提供根据用户名查询用户信息的功能
 * JwtAuthenticationProvider在进行登录信息校验时就会通过它查询用户信息
 *
 * @author liudong
 */
@Component
public class JwtUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysRoleMapper roleMapper;


    /**
     * 模拟数据库查询
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysUser.FILED_USERNAME, username);
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            // 定制异常抛出 todo
            throw new UsernameNotFoundException("用户信息错误");
        }

        QueryWrapper<SysUserRole> roleQueryWrapper = new QueryWrapper<>();
        SysUserRole entity = new SysUserRole();
        entity.setSysUserId(user.getId());
        roleQueryWrapper.setEntity(entity);
        List<SysUserRole> userRoles = userRoleMapper.selectList(roleQueryWrapper);
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toList());
        List<SysRole> roles = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleIds)) {
            roles = roleMapper.selectBatchIds(roleIds);
        }
        List<SimpleGrantedAuthority> authorities = roles.stream().map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());

        return new JwtUser(user.getUsername(), user.getPassword(), authorities);

    }
}
