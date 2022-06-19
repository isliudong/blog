package com.ld.blog.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author liudong
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogConfig {
    private Integer port = 8000;
    private String fileSeverPath = "/";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public UserCache userCache() {
        return new EhCacheBasedUserCache();
    }*/
}
