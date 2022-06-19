package com.ld.blog.app.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.api.controller.v1.dto.CommentDTO;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.domain.entity.ArticleComment;
import com.ld.blog.infra.constants.CommentType;
import com.ld.blog.infra.mapper.ArticleCommentMapper;
import com.ld.blog.infra.mapper.ArticleMapper;
import com.ld.blog.infra.mapper.SysUserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author liudong
 */
@Service
@Slf4j
@AllArgsConstructor
public class ArticleCommentService {
    private final ArticleCommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final SysUserMapper userMapper;

    public Page<CommentDTO> getComments(Page<ArticleComment> page, Long articleId) {
        QueryWrapper<ArticleComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ArticleComment.FIELDS_PARENT_TYPE, CommentType.ARTICLE);
        queryWrapper.eq(ArticleComment.FIELDS_ARTICLE_ID, articleId);
        Page<ArticleComment> articleCommentPage = commentMapper.selectPage(page, queryWrapper);
        for (ArticleComment articleComment : articleCommentPage.getRecords()) {
            initChildren(articleComment);
        }
        return null;
    }

    private void initChildren(ArticleComment articleComment) {
        QueryWrapper<ArticleComment> wrapper = new QueryWrapper<>();
        wrapper.eq(ArticleComment.FIELDS_PARENT_TYPE, CommentType.SUB);
        wrapper.eq(ArticleComment.FIELDS_PARENT_ID, articleComment.getId());
        List<ArticleComment> subComments = commentMapper.selectList(wrapper);
        //articleComment.set
    }

    public Page<ArticleComment> getArticleCommentPage(Page<ArticleComment> page, ArticleComment comment, Long articleId) {
        Article article = articleMapper.selectById(articleId);
        comment.setArticleId(articleId);
        QueryWrapper<ArticleComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(comment);
        Page<ArticleComment> articleCommentPage = commentMapper.selectPage(page, queryWrapper);

        for (ArticleComment articleComment : articleCommentPage.getRecords()) {
            initSub(article, articleComment);
        }
        return articleCommentPage;
    }

    private void initSub(Article article, ArticleComment articleComment) {
        if (articleComment.getReplyToUser() != null) {
            ArticleComment.User reply = new ArticleComment.User(userMapper.selectById(articleComment.getReplyToUser()));
            articleComment.setReply(reply);
        }
        ArticleComment.User user = new ArticleComment.User(userMapper.selectById(articleComment.getCreateBy()));
        if (articleComment.getCreateBy().equals(article.getCreateBy())) {
            user.setAuthorFlag(true);
        }
        articleComment.setUser(user);
        QueryWrapper<ArticleComment> wrapper = new QueryWrapper<>();
        wrapper.eq(ArticleComment.FIELDS_PARENT_TYPE, "COMMENT");
        wrapper.eq(ArticleComment.FIELDS_PARENT_ID, articleComment.getId());
        List<ArticleComment> subArticleComments = commentMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(subArticleComments)) {
            return;
        }
        articleComment.setChildren(subArticleComments);
        for (ArticleComment subArticleComment : subArticleComments) {
            initSub(article, subArticleComment);
        }
    }


    public void likeComment(Long id) {
        // todo 加锁
        ArticleComment comment = commentMapper.selectById(id);
        ArticleComment build = ArticleComment
                .builder()
                .id(id)
                .likes(comment.getLikes() + 1)
                .build();
        commentMapper.updateById(build);
    }

    @Transactional(rollbackFor = Exception.class)
    public void comment(ArticleComment comment) {
        commentMapper.insert(comment);
        Article article = articleMapper.selectById(comment.getArticleId());
        Article build = Article
                .builder()
                .id(comment.getArticleId())
                .comments(article.getComments() + 1)
                .build();
        articleMapper.updateById(build);
    }
}
