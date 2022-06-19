package com.ld.blog.domain.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * user_favorite_article_rel
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserFavoriteArticleRel extends AuditDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long articleId;
}
