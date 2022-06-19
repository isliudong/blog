package com.ld.blog;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author liudong
 */
@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = {"com.ld.blog.**.mapper"})
public class BlogApplication {

    public static void main(String[] args) {
        ElasticApmAttacher.attach();
        SpringApplication.run(BlogApplication.class, args);
    }

}
