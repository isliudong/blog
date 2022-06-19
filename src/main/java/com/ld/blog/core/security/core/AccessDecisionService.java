package com.ld.blog.core.security.core;


import static com.ld.blog.core.security.core.Authority.publicPer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 配置路径访问限制,若你的用户角色比较简单,不需要存数据库,
 * 可以在ApplicationConfigurerAdapter里配置如
 * httpSecurity
 * .authorizeRequests()
 * .antMatchers("/order").....
 *
 * @author liudong
 */
@Component("accessDecisionService")
public class AccessDecisionService {


    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    private Authority authority;

    public boolean hasPermission(HttpServletRequest request, Authentication auth) {

        //不在权限接口中
        for (String url : publicPer) {
            if (antPathMatcher.match(url, request.getMethod().toLowerCase() + request.getRequestURI())) {
                return true;
            }
        }
        //未登录
        if (auth instanceof AnonymousAuthenticationToken) {
            return false;
        }

        //登录权限
        auth.getAuthorities();
        UserDetails user = (UserDetails) auth.getPrincipal();


        boolean acess = false;
        for (GrantedAuthority userAuthority : user.getAuthorities()) {
            if ("admin".equals(userAuthority.getAuthority())) {
                return true;
            }
            acess = authority.hasAuthority(userAuthority.getAuthority(),
                    request.getMethod().toLowerCase() + request.getRequestURI());
            if (acess) {
                break;
            }
        }
        return acess;
    }
}
