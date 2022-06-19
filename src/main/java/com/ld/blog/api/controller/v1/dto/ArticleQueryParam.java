package com.ld.blog.api.controller.v1.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liudong
 * @since 2022-04-03 12:08:50
 */
@Getter
@Setter
public class ArticleQueryParam {


    private String title;

    private String description;

    private Long author;

    private Long views;

    private Long comments;

    private Long likes;

    private String status;

    private String keyword;

    private String sort;

    private List<String> ids;

    private boolean highlight;


    private Long tagId;


}
