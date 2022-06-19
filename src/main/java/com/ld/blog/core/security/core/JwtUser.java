package com.ld.blog.core.security.core;

import java.util.Collection;
import java.util.List;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户实体,实现UserDetails,UserDetails为springSecurity默认的用户实体抽象
 *
 * @author liudong
 */
@Data
public class JwtUser implements UserDetails {
    private String username;
    private String password;
    private String token;
    private List<SimpleGrantedAuthority> authorities;

    public JwtUser() {
    }

    public JwtUser(String username, String password, List<SimpleGrantedAuthority> roles) {
        this.username = username;
        this.password = password;
        this.authorities = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
