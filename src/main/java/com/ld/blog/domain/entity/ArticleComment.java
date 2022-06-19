package com.ld.blog.domain.entity;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * article_comment
 */
@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArticleComment extends AuditDomain implements Serializable {
    public static final String FIELDS_ID = "id";
    public static final String FIELDS_ARTICLE_ID = "article_id";
    public static final String FIELDS_PARENT_TYPE = "parent_type";
    public static final String FIELDS_PARENT_ID = "parent_id";
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    @Length(max = 1000)
    private String content;

    /**
     * 评论中的图片
     */
    @TableField(exist = false)
    private String imgSrc;

    private Long parentId;

    private String parentType;

    private Long articleId;

    private Long replyToUser;

    private Integer likes;


    @TableField(exist = false)
    private List<ArticleComment> children;

    /**
     * 被回复人
     */
    @TableField(exist = false)
    private User reply;

    /**
     * 评论人
     */
    @TableField(exist = false)
    private User user;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class User implements Serializable {
        private Boolean authorFlag = false;
        private Long id = 1L;
        private String avatar = "/src/assets/image/avatar1.jpg";
        private String username = "testname";
        private String email = "123@qq.com";

        public User(SysUser sysUser) {
            id = sysUser.getId();
            avatar = sysUser.getAvatar();
            username = sysUser.getUsername();
            email = sysUser.getEmail();
        }
    }
}
