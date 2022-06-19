package com.ld.blog.app.service;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.ArticlePub;
import com.ld.blog.api.controller.v1.dto.ArticleQueryParam;
import com.ld.blog.core.exception.CommonException;
import com.ld.blog.core.security.core.UserContext;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.domain.entity.ArticleTagRel;
import com.ld.blog.domain.entity.SysUser;
import com.ld.blog.domain.entity.Tag;
import com.ld.blog.domain.entity.UserLikeArticleRel;
import com.ld.blog.domain.repository.ArticleRepository;
import com.ld.blog.infra.mapper.ArticleCommentMapper;
import com.ld.blog.infra.mapper.ArticleMapper;
import com.ld.blog.infra.mapper.ArticleTagRelMapper;
import com.ld.blog.infra.mapper.SysUserMapper;
import com.ld.blog.infra.mapper.TagMapper;
import com.ld.blog.infra.mapper.UserLikeArticleRelMapper;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liudong
 */
@Service
@Slf4j
@AllArgsConstructor
public class ArticleService {
    private final ArticleCommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final ArticleRepository articleRepository;
    private final TagMapper tagMapper;
    private final ArticleTagRelMapper articleTagRelMapper;
    private final SysUserMapper userMapper;
    private final UserContext userContext;
    private final ArticleSearchService articleSearchService;
    private final RoleService roleService;
    private final UserLikeArticleRelMapper userLikeArticleRelMapper;


    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    public void likeArticle(Long id) {
        UserLikeArticleRel likeArticleRel = UserLikeArticleRel.builder().articleId(id).build();
        likeArticleRel.setCreateBy(userContext.getCurrentUser().getId());
        QueryWrapper<UserLikeArticleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(likeArticleRel);
        UserLikeArticleRel likeArticleRel1 = userLikeArticleRelMapper.selectOne(queryWrapper);
        if (likeArticleRel1 != null) {
            throw new CommonException("已经点过赞了");
        }
        userLikeArticleRelMapper.insert(UserLikeArticleRel.builder().articleId(id).build());
        Article article = articleMapper.selectById(id);
        Article build = Article
                .builder()
                .id(id)
                .likes(article.getLikes() + 1)
                .build();
        articleMapper.updateById(build);
    }

    @Transactional(rollbackFor = Exception.class)
    public void insert(Article article) {
        articleMapper.insert(article);
        saveTags(article);
    }

    public Page<Article> selectList(Page<Article> page, ArticleQueryParam param) {
        return selectList(page, param, null);
    }

    public Page<ArticlePub> selectPubList(Page<ArticlePub> page, ArticleQueryParam param) {
        // todo 扩展mybatis实现动态替换发布表
        return selectPubList(page, param, null);
    }

    public Page<Article> selectList(Page<Article> page, ArticleQueryParam param, @Nullable Long userId) {
        List<ArticleTagRel> rels1 = null;
        if (param.getTagId() != null) {
            QueryWrapper<ArticleTagRel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ArticleTagRel.FILED_TAG_ID, param.getTagId());
            rels1 = articleTagRelMapper.selectList(queryWrapper);
        }
        List<String> ids;
        if (rels1 != null && CollUtil.isNotEmpty(rels1)) {
            ids = rels1.stream().map(r -> r.getArticleId().toString()).collect(Collectors.toList());
            param.setIds(ids);
        }
        SysUser currentUser = userContext.getCurrentUser();
        if(currentUser!=null&&roleService.isAdmin(currentUser.getId())){
            userId=null;
        }
        Page<Article> articlePage = articleSearchService.searchArticle(page, param, userId);

        if (currentUser == null) {
            return articlePage;
        }
        for (Article articleRecord : articlePage.getRecords()) {
            initUserLikedStatus(currentUser, articleRecord);
        }
        return articlePage;
    }

    public Page<ArticlePub> selectPubList(Page<ArticlePub> page, ArticleQueryParam param, @Nullable Long userId) {
        List<ArticleTagRel> rels1 = null;
        if (param.getTagId() != null) {
            QueryWrapper<ArticleTagRel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(ArticleTagRel.FILED_TAG_ID, param.getTagId());
            rels1 = articleTagRelMapper.selectList(queryWrapper);
        }
        List<String> ids = null;
        if (rels1 != null && CollUtil.isNotEmpty(rels1)) {
            ids = rels1.stream().map(r -> r.getArticleId().toString()).collect(Collectors.toList());
        }
        Page<ArticlePub> articlePage = articleSearchService.searchPubArticle(page, param.getKeyword(), userId, ids);

        SysUser currentUser = userContext.getCurrentUser();
        if (currentUser == null) {
            return articlePage;
        }
        for (ArticlePub articleRecord : articlePage.getRecords()) {
            initUserLikedStatus(currentUser, articleRecord);
        }
        return articlePage;
    }

    private void initUserLikedStatus(SysUser currentUser, Article articleRecord) {
        if (currentUser == null) {
            return;
        }
        QueryWrapper<UserLikeArticleRel> wrapper = new QueryWrapper<>();
        UserLikeArticleRel build = UserLikeArticleRel.builder().articleId(articleRecord.getId()).build();
        build.setCreateBy(currentUser.getId());
        wrapper.setEntity(build);
        UserLikeArticleRel likeArticleRel = userLikeArticleRelMapper.selectOne(wrapper);
        articleRecord.setUserLiked(likeArticleRel != null);
    }

    private void initUserLikedStatus(SysUser currentUser, ArticlePub articleRecord) {
        if (currentUser == null) {
            return;
        }
        QueryWrapper<UserLikeArticleRel> wrapper = new QueryWrapper<>();
        UserLikeArticleRel build = UserLikeArticleRel.builder().articleId(articleRecord.getId()).build();
        build.setCreateBy(currentUser.getId());
        wrapper.setEntity(build);
        UserLikeArticleRel likeArticleRel = userLikeArticleRelMapper.selectOne(wrapper);
        articleRecord.setUserLiked(likeArticleRel != null);
    }

    public Article detail(Long id) {
        Article article = articleMapper.selectById(id);
        initUserLikedStatus(userContext.getCurrentUser(), article);
        List<Tag> tags = tagMapper.selectArticleTags(id);
        article.setTags(tags);
        return article;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        articleRepository.deleteByPrimaryKey(id);
        QueryWrapper<ArticleTagRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArticleTagRel.FILED_ARTICLE_ID, id);
        articleTagRelMapper.delete(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Article article) {
        QueryWrapper<Article> updateWrapper = new QueryWrapper<>();
        updateWrapper.setEntity(Article.builder().id(article.getId()).build());
        articleMapper.update(article, updateWrapper);

        //删除原始标签
        QueryWrapper<ArticleTagRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArticleTagRel.FILED_ARTICLE_ID, article.getId());
        articleTagRelMapper.delete(queryWrapper);

        //存新标签
        saveTags(article);
    }

    @Synchronized
    private void saveTags(Article article) {
        if (CollUtil.isNotEmpty(article.getTags())) {
            article.getTags().forEach(t -> {
                if (t.getId() == null) {
                    QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq(Tag.FILED_NAME, t.getName());
                    Tag dbTag = tagMapper.selectOne(queryWrapper);
                    if (dbTag != null) {
                        t = dbTag;
                    } else {
                        tagMapper.insert(t);
                    }
                    ArticleTagRel build = ArticleTagRel
                            .builder()
                            .articleId(article.getId())
                            .tagId(t.getId())
                            .build();
                    articleTagRelMapper.insert(build);
                } else {
                    ArticleTagRel build = ArticleTagRel
                            .builder()
                            .articleId(article.getId())
                            .tagId(t.getId())
                            .build();
                    articleTagRelMapper.insert(build);
                }
            });
        }
    }

    public void cancelLikeArticle(Long id) {
        UserLikeArticleRel likeArticleRel = UserLikeArticleRel.builder().articleId(id).build();
        likeArticleRel.setCreateBy(userContext.getCurrentUser().getId());
        QueryWrapper<UserLikeArticleRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(likeArticleRel);
        UserLikeArticleRel likeArticleRel1 = userLikeArticleRelMapper.selectOne(queryWrapper);
        if (likeArticleRel1 == null) {
            throw new CommonException("没点过赞兄弟");
        }
        userLikeArticleRelMapper.deleteById(likeArticleRel1.getId());
        Article article = articleMapper.selectById(id);
        Article build = Article
                .builder()
                .id(id)
                .likes(article.getLikes() - 1)
                .build();
        articleMapper.updateById(build);
    }

    @Synchronized
    public void viewArticle(Long id) {
        Article article = articleMapper.selectById(id);
        Article build = Article
                .builder()
                .id(id)
                .views(article.getViews() + 1)
                .build();
        articleMapper.updateById(build);
    }
}
