package com.ld.blog.admin.domain.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.domain.entity.AuditDomain;
import com.ld.blog.domain.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * (ArticlePub)实体类
 *
 * @author liudong
 * @since 2022-03-23 23:14:37
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ArticlePub extends AuditDomain {
    public static final String FIELD_ID = "id";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_IMG = "img";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_VIEWS = "views";
    public static final String FIELD_COMMENTS = "comments";
    public static final String FIELD_LIKES = "likes";
    public static final String FIELD_CREATE_DATE = "createDate";
    public static final String FIELD_LAST_UPDATED_DATE = "lastUpdatedDate";
    public static final String FIELD_CREATE_BY = "createBy";
    private static final long serialVersionUID = 570573933788601566L;
    private Long id;

    private Object description;

    private String img;

    private String title;

    private Long author;

    private String content;

    private Long views;

    private Long comments;

    private Long likes;


    @TableField(exist = false)
    private Article.Meta meta = new Article.Meta();

    @TableField(exist = false)
    private String status = "published";

    @TableField(exist = false)
    private List<Tag> tags;

    @TableField(exist = false)
    private Long tagId;

    @TableField(exist = false)
    private Boolean userLiked;

    @Data
    public static class Meta {
        Integer views = 1;
        Integer comments = 1;
        Integer likes = 1;
    }

}
