package com.ld.blog.app.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.api.controller.v1.dto.ArticleQueryParam;
import com.ld.blog.api.controller.v1.dto.ArticleSearchDTO;
import com.ld.blog.domain.entity.Article;

/**
 * 搜索策略
 *
 * @author liudong
 * @date 2021/07/27
 */
public interface SearchStrategy {

    /**
     * 搜索文章
     *
     * @param page
     * @param param    关键字
     * @param createBy
     * @return {@link List<ArticleSearchDTO>} 文章列表
     */
    Page<Article> searchArticle(Page<Article> page, ArticleQueryParam param, Long createBy);

}
