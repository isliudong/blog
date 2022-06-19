package com.ld.blog.domain.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * user_like_article_rel
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class UserLikeArticleRel extends AuditDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long articleId;
}
