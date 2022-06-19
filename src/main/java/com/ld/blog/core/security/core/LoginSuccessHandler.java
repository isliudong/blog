package com.ld.blog.core.security.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 登录成功的处理类,被JwtLoginFilter调用,
 * 并把JwtAuthenticationProvider创建的凭证(JwtLoginToken)传给它,
 * 它就可以根据凭证里的认证信息进行登录成功的处理,如生成token等
 *
 * @author liudong
 */
@Component
@AllArgsConstructor
@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RsaSigner signer;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
        JwtUser user = new JwtUser(authentication.getName(), null, new ArrayList<>(authorities));
        user.setPassword(null);
        String userJsonStr = JSON.toJSONString(user);
        String token = JwtHelper.encode(userJsonStr, signer).getEncoded();
        //签发token
        user.setToken(token);
        response.getWriter().write(JSON.toJSONString(user));
    }
}
