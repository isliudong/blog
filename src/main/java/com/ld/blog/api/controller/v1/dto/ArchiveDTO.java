package com.ld.blog.api.controller.v1.dto;

import java.util.List;

import com.ld.blog.domain.entity.Article;
import lombok.Data;

/**
 * @author liudong
 */
@Data
public class ArchiveDTO {
    private Long year;
    private List<Article> articles;
}
