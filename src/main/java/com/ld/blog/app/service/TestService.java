package com.ld.blog.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.infra.mapper.PermissionMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author liudong
 */
@Service
@Slf4j
@AllArgsConstructor
public class TestService {
    private final PermissionMapper permissionMapper;

    public void test() {

        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
        permissionMapper.selectList(queryWrapper);
    }

}
