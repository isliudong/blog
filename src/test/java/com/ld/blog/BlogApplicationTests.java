package com.ld.blog;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan(basePackages = "com.ld.blog.mapper")
class BlogApplicationTests {

    @Test
    void contextLoads() {
    }

}
