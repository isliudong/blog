package com.ld.blog.app.service;

import static com.ld.blog.infra.constants.CommonConst.POST_TAG;
import static com.ld.blog.infra.constants.CommonConst.PRE_TAG;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.ArticlePub;
import com.ld.blog.admin.infra.mapper.ArticlePubMapper;
import com.ld.blog.api.controller.v1.dto.ArticleQueryParam;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.domain.entity.AuditDomain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liudong
 */
@Service
@Slf4j
public class ArticleSearchService {

    @Autowired
    private Map<String, SearchStrategy> searchStrategyMap;
    @Autowired
    private ArticlePubMapper articlePubMapper;


    public Page<Article> searchArticle(Page<Article> page, ArticleQueryParam param, Long createBy) {
        //流量不大暂时使用mysql fullText
        return searchStrategyMap.get("mySqlSearchStrategyImpl").searchArticle(page, param, createBy);
    }


    public Page<ArticlePub> searchPubArticle(Page<ArticlePub> page, String keyword, Long userId, List<String> ids) {
        // 搜索文章
        LambdaQueryWrapper<ArticlePub> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(i -> i.like(ArticlePub::getTitle, keyword).or().like(ArticlePub::getContent, keyword));
        }
        if (CollectionUtils.isNotEmpty(ids)) {
            queryWrapper.and(i -> i.in(ArticlePub::getId, ids));
        }
        if (userId != null) {
            queryWrapper.and(i -> i.eq(AuditDomain::getCreateBy, userId));

        }
        Page<ArticlePub> articlePage = articlePubMapper.selectPage(page, queryWrapper);
        // 高亮处理
        if (StringUtils.isBlank(keyword)) {
            return articlePage;
        }
        articlePage.getRecords().forEach(item -> {
            // 获取关键词第一次出现的位置
            String articleContent = item.getContent();
            int index = item.getContent().indexOf(keyword);
            if (index != -1) {
                // 获取关键词前面的文字
                int preIndex = index > 25 ? index - 25 : 0;
                String preText = item.getContent().substring(preIndex, index);
                // 获取关键词到后面的文字
                int last = index + keyword.length();
                int postLength = item.getContent().length() - last;
                int postIndex = postLength > 175 ? last + 175 : last + postLength;
                String postText = item.getContent().substring(index, postIndex);
                // 文章内容高亮
                articleContent = (preText + postText).replaceAll(keyword, PRE_TAG + keyword + POST_TAG);
            }
            // 文章标题高亮
            String articleTitle = item.getTitle().replaceAll(keyword, PRE_TAG + keyword + POST_TAG);
            item.setTitle(articleTitle);
            item.setContent(articleContent);
        });

        return articlePage;
    }
}
