package com.ld.blog.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ld.blog.app.service.UserService;
import com.ld.blog.domain.entity.SysRole;
import com.ld.blog.domain.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.dcenter.ums.security.core.oauth.enums.ErrorCodeEnum;
import top.dcenter.ums.security.core.oauth.exception.RegisterUserFailureException;
import top.dcenter.ums.security.core.oauth.exception.UserNotExistException;
import top.dcenter.ums.security.core.oauth.service.UmsUserDetailsService;

/**
 * 三方用户登录与注册服务：<br><br>
 * 1. 用于第三方登录与手机短信登录逻辑。<br><br>
 * 2. 用于用户密码登录逻辑。<br><br>
 * 3. 用户注册逻辑。<br><br>
 *
 * @author liudong
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UmsUserDetailsService {


    @Autowired(required = false)
    private UserCache userCache;

    @Autowired
    private UserService userService;
    /**
     * 用于密码加解密
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            // 从缓存中查询用户信息:
            // 从缓存中查询用户信息
            if (this.userCache != null) {
                UserDetails userDetails = this.userCache.getUserFromCache(username);
                if (userDetails != null) {
                    return userDetails;
                }
            }
            // 根据用户名获取用户信息
            SysUser user = userService.getUser(username);
            // 获取用户信息逻辑。。。

            log.info("Demo ======>: 登录用户名：{}, 登录成功", username);
            List<SimpleGrantedAuthority> authorities = user.getRoles()
                    .stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
            return new User(username,
                    passwordEncoder.encode(user.getPassword()),
                    true,
                    true,
                    true,
                    BooleanUtils.isNotTrue(user.getLocked()),
                    authorities);

        } catch (Exception e) {
            String msg = String.format("Demo ======>: 登录用户名：%s, 登录失败: %s", username, e.getMessage());
            log.error(msg);
            throw new UserNotExistException(ErrorCodeEnum.QUERY_USER_INFO_ERROR, e, username);
        }
    }

    @Override
    public UserDetails registerUser(AuthUser authUser, String username, String defaultAuthority, String decodeState) throws RegisterUserFailureException {

        // 第三方授权登录不需要密码, 这里随便设置的, 生成环境按自己的逻辑
        String encodedPassword = passwordEncoder.encode(authUser.getUuid());

        // 这里的 decodeState 可以根据自己实现的 top.dcenter.ums.security.core.oauth.service.Auth2StateCoder 接口的逻辑来传递必要的参数.
        // 比如: 第三方登录成功后的跳转地址
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 假设 decodeState 就是 redirectUrl, 我们直接把 redirectUrl 设置到 request 上
        // 后续经过成功处理器时直接从 requestAttributes.getAttribute("redirectUrl", RequestAttributes.SCOPE_REQUEST) 获取并跳转
        if (requestAttributes != null) {
            requestAttributes.setAttribute("redirectUrl", decodeState, RequestAttributes.SCOPE_REQUEST);
        }
        // 当然 decodeState 也可以传递从前端传到后端的用户信息, 注册到本地用户


        // ... 用户注册逻辑
        SysUser newUser = SysUser.builder()
                .username(username)
                .usercode(username)
                .password(encodedPassword)
                .email(authUser.getEmail())
                .avatar(authUser.getAvatar())
                .build();
        userService.createUser(newUser);
        SysUser userDb = userService.getUser(username);
        log.info("Demo ======>: 用户名：{}, 注册成功", username);

        // @formatter:off
        List<SysRole> roles = userDb.getRoles();
        List<SimpleGrantedAuthority> authorities;
        if (CollectionUtils.isNotEmpty(roles)) {
            authorities = roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
        } else {
            authorities = new ArrayList<>();
        }
        UserDetails user = User.builder()
                .username(username)
                .password(encodedPassword)
                .disabled(false)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .authorities(authorities)
                .build();
        // @formatter:off

        // 把用户信息存入缓存
        if (userCache != null) {
            userCache.putUserInCache(user);
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        UserDetails userDetails = loadUserByUsername(userId);
        User.withUserDetails(userDetails);
        return User.withUserDetails(userDetails).build();
    }

    /**
     * {@link #existedByUsernames(String...)} usernames 生成规则.
     * 如需自定义重新实现此逻辑
     *
     * @param authUser 第三方用户信息
     * @return 返回一个 username 数组
     */
    @Override
    public String[] generateUsernames(AuthUser authUser) {
        return new String[]{
                authUser.getUsername(),
                // providerId = authUser.getSource()
                authUser.getUsername() + "_" + authUser.getSource(),
                // providerUserId = authUser.getUuid()
                authUser.getUsername() + "_" + authUser.getSource() + "_" + authUser.getUuid()
        };
    }

    @Override
    public List<Boolean> existedByUsernames(String... usernames) throws UsernameNotFoundException {
        // ... 在本地账户上查询 userIds 是否已被使用
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < usernames.length; i++) {
            list.add(!userService.checkNameUsable(usernames[i]));
        }
        return list;
    }

}
