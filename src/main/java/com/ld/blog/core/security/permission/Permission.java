package com.ld.blog.core.security.permission;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 权限定义注释
 * <p>
 * 加入权限状态配置
 *
 * @author liudong
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * API编码
     *
     * @return 权限编码
     */
    String code() default "";

    /**
     * 角色
     *
     * @return 角色数组
     */
    String[] roles() default {};

    /**
     * 级别
     *
     * @return 级别
     */
    ResourceLevel level() default ResourceLevel.PROJECT;

    /**
     * 登陆后即可拥有的权限
     *
     * @return 是否登录可访问接口
     */
    boolean permissionLogin() default false;

    /**
     * 公共权限，不需要登录就可访问
     *
     * @return 是否为公开接口
     */
    boolean permissionPublic() default false;

}

