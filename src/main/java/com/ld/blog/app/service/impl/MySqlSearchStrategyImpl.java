package com.ld.blog.app.service.impl;

import static com.ld.blog.infra.constants.CommonConst.POST_TAG;
import static com.ld.blog.infra.constants.CommonConst.PRE_TAG;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.api.controller.v1.dto.ArticleQueryParam;
import com.ld.blog.app.service.SearchStrategy;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.domain.entity.AuditDomain;
import com.ld.blog.infra.mapper.ArticleMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * mysql搜索策略
 *
 * @author liudong
 * @date 2021/07/27
 */
@Service
@AllArgsConstructor
public class MySqlSearchStrategyImpl implements SearchStrategy {
    private ArticleMapper articleMapper;

    @Override
    public Page<Article> searchArticle(Page<Article> page, ArticleQueryParam param, Long createBy) {
        // 搜索文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntity(Article.builder()
                .status(param.getStatus())
                .author(param.getAuthor())
                .title(param.getTitle())
                .build());
        if (StringUtils.isNotBlank(param.getKeyword())) {
            queryWrapper.and(i -> i.like(Article::getTitle, param.getKeyword()).or().like(Article::getContent, param.getKeyword()));
        }
        if (CollectionUtils.isNotEmpty(param.getIds())) {
            queryWrapper.and(i -> i.in(Article::getId, param.getIds()));
        }
        if (createBy != null) {
            queryWrapper.and(i -> i.eq(AuditDomain::getCreateBy, createBy));

        }
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        // 高亮处理
        if (StringUtils.isBlank(param.getKeyword()) || !param.isHighlight()) {
            return articlePage;
        }
        articlePage.getRecords().forEach(item -> {
            // 获取关键词第一次出现的位置
            String articleContent = item.getContent();
            int index = item.getContent().indexOf(param.getKeyword());
            if (index != -1) {
                // 获取关键词前面的文字
                int preIndex = index > 25 ? index - 25 : 0;
                String preText = item.getContent().substring(preIndex, index);
                // 获取关键词到后面的文字
                int last = index + param.getKeyword().length();
                int postLength = item.getContent().length() - last;
                int postIndex = postLength > 175 ? last + 175 : last + postLength;
                String postText = item.getContent().substring(index, postIndex);
                // 文章内容高亮
                articleContent = (preText + postText).replaceAll(param.getKeyword(), PRE_TAG + param.getKeyword() + POST_TAG);
            }
            // 文章标题高亮
            String articleTitle = item.getTitle().replaceAll(param.getKeyword(), PRE_TAG + param.getKeyword() + POST_TAG);
            item.setTitle(articleTitle);
            item.setContent(articleContent);
        });

        return articlePage;
    }

}
