package com.ld.blog.api.controller.v1.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liudong
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Long id = 1L;
    private String content = "test";
    private String imgSrc = "/src/assets/image/avatar1.jpg";
    private List<CommentDTO> children;
    private Long likes = 3L;
    private Commenter reply = new Commenter();
    private Date createAt = new Date();
    private Commenter user = new Commenter();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Commenter {
        private Boolean author = false;
        private Long id = 1L;
        private String avatar = "/src/assets/image/avatar1.jpg";
        private String name = "testname";
        private String email = "123@qq.com";
    }
}
