package com.ld.blog.core.security.permission;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 应用权限初始化
 *
 * @author liudong
 */
@Component
public class ApplicationPermissionInit implements SmartInitializingSingleton {
    /**
     * 应用权限解析器对象
     */
    private ApplicationPermissionParser applicationPermissionParser;

    @Autowired
    public void setApplicationPermissionParser(ApplicationPermissionParser applicationPermissionParser) {
        this.applicationPermissionParser = applicationPermissionParser;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 解析权限数据
        this.applicationPermissionParser.parse();
    }
}

