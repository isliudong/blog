package com.ld.blog.core.security.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 自定义的登录过滤器,把它加到SpringSecurity的过滤链中,拦截登录请求它干的事有
 * <p>
 * 1. 设置登录的url,请求的方式,其实也就是定义这个过滤器要拦截哪个请求
 * 2. 调用JwtAuthenticationProvider进行登录校验
 * 3. 校验成功调用LoginSuccessHandler,校验失败调用LoginSuccessHandler
 *
 * @author liudong
 */
@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {


    public JwtLoginFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            JSONObject parameters = getJsonBody(request);
            String userName = parameters.getString("username");
            String password = parameters.getString("password");
            //创建未认证的凭证(etAuthenticated(false)),注意此时凭证中的主体principal为用户名
            JwtLoginToken jwtLoginToken = new JwtLoginToken(userName, password);
            //将认证详情(ip,sessionId)写到凭证
            jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
            //AuthenticationManager获取受支持的AuthenticationProvider(这里也就是JwtAuthenticationProvider),
            //生成已认证的凭证,此时凭证中的主体为userDetails
            return this.getAuthenticationManager().authenticate(jwtLoginToken);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private JSONObject getJsonBody(HttpServletRequest request) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return JSON.parseObject(responseStrBuilder.toString());
    }

}
