package com.ld.blog.core.security.config;

import com.alibaba.fastjson.JSON;
import com.ld.blog.core.exception.Result;
import com.ld.blog.core.security.core.JwtAuthenticationProvider;
import com.ld.blog.core.security.core.JwtHeadFilter;
import com.ld.blog.core.security.core.JwtLoginFilter;
import com.ld.blog.core.security.core.JwtUserDetailServiceImpl;
import com.ld.blog.core.security.core.LoginFailureHandler;
import com.ld.blog.core.security.core.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.dcenter.ums.security.core.oauth.config.Auth2AutoConfigurer;
import top.dcenter.ums.security.core.oauth.properties.Auth2Properties;

/**
 * @author liudong
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RsaVerifier verifier;
    @Autowired
    private RsaSigner signer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Auth2Properties auth2Properties;

    @Autowired
    private Auth2AutoConfigurer auth2AutoConfigurer;

    @Autowired
    private JwtUserDetailServiceImpl jwtUserDetailService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //登录过滤器
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter();
        jwtLoginFilter.setAuthenticationManager(this.authenticationManagerBean());

        //登录成功和失败的操作
        jwtLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        jwtLoginFilter.setAuthenticationFailureHandler(loginFailureHandler);

        //登录过滤器的授权提供者(就这么叫吧)
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider();
        jwtAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        jwtAuthenticationProvider.setUserDetailsService(jwtUserDetailService);

        //JWT校验过滤器
        JwtHeadFilter headFilter = new JwtHeadFilter();
        headFilter.setVerifier(verifier);

        http
                //身份验证入口,当需要登录却没登录时调用
                //具体为,当抛出AccessDeniedException异常时且当前是匿名用户时调用
                //匿名用户: 当过滤器链走到匿名过滤器(AnonymousAuthenticationFilter)时,
                //会进行判断SecurityContext是否有凭证(Authentication),若前面的过滤器都没有提供凭证,
                //匿名过滤器会给SecurityContext提供一个匿名的凭证(可以理解为用户名和权限为anonymous的Authentication),
                //这也是JwtHeadFilter发现请求头中没有jwtToken不作处理而直接进入下一个过滤器的原因
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().append(JSON.toJSONString(Result.errorMsg("请登录")));
                })

                //拒绝访问处理,当已登录,但权限不足时调用
                //抛出AccessDeniedException异常时且当不是匿名用户时调用
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().append(JSON.toJSONString(Result.errorMsg("权限不足")));
                })
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        auth2Properties.getRedirectUrlPrefix() + "/*",
                        auth2Properties.getAuthLoginUrlPrefix() + "/*")
                .permitAll()
                .anyRequest()
                .access("@accessDecisionService.hasPermission(request , authentication)")
                .and()

                //将授权提供者注册到授权管理器中(AuthenticationManager)
                .authenticationProvider(jwtAuthenticationProvider)
                .addFilterAfter(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(headFilter, JwtLoginFilter.class)
                //禁用session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();

        http.apply(this.auth2AutoConfigurer);
    }
}
