package com.ld.blog.domain.entity;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

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
 * article
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article extends AuditDomain implements Serializable {
    public static final String FILED_ID = "id";
    public static final String FILED_CONTENT = "content";
    public static final String FILED_TITLE = "title";
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    @Length(max = 30)
    @NotBlank
    private String title;
    @Length(max = 1000)
    @NotBlank
    private String description;
    @Length(max = 200)
    private String img;
    private Long author;
    private Long views;
    private Long comments;
    private Long likes;
    private String status;
    @TableField(exist = false)
    private Meta meta;
    @TableField(exist = false)
    private List<Tag> tags;
    @TableField(exist = false)
    private Long tagId;
    @TableField(exist = false)
    private Boolean userLiked;
    @Length(max = 1000000)
    private String content;

    @Data
    public static class Meta implements Serializable {
        Integer views = 1;
        Integer comments = 1;
        Integer likes = 1;
    }
}
