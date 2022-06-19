package com.ld.blog.domain.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * article_tag_rel
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ArticleTagRel extends AuditDomain implements Serializable {
    public static final String FILED_ID = "id";
    public static final String FILED_TAG_ID = "tag_id";
    public static final String FILED_ARTICLE_ID = "article_id";
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long tagId;
    private Long articleId;
}
