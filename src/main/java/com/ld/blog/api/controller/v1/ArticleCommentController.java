package com.ld.blog.api.controller.v1;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.app.service.ArticleCommentService;
import com.ld.blog.core.security.permission.Permission;
import com.ld.blog.domain.entity.ArticleComment;
import com.ld.blog.infra.mapper.ArticleCommentMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liudong
 */
@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class ArticleCommentController {

    ArticleCommentMapper commentMapper;
    ArticleCommentService commentService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<ArticleComment>> getList(Page<ArticleComment> page,
                                                        ArticleComment comment, @PathVariable Long articleId) {


        Page<ArticleComment> articleCommentPage = commentService.getArticleCommentPage(page, comment, articleId);
        return ResponseEntity.ok(articleCommentPage);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ArticleComment> detail(@PathVariable Long id) {
        return ResponseEntity.ok(commentMapper.selectById(id));
    }

    @Permission(permissionLogin = true)
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody @Validated ArticleComment comment) {
        if ("ARTICLE".equals(comment.getParentType())) {
            comment.setArticleId(comment.getParentId());
        }
        if (comment.getReply() != null) {
            comment.setReplyToUser(comment.getReply().getId());
        }
        commentService.comment(comment);
        return ResponseEntity.ok(1);
    }

    @Permission
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable Long id, @RequestBody @Validated ArticleComment comment) {
        QueryWrapper<ArticleComment> updateWrapper = new QueryWrapper<>();
        updateWrapper.setEntity(ArticleComment.builder().id(id).build());
        return ResponseEntity.ok(commentMapper.update(comment, updateWrapper));
    }

    @Permission(permissionLogin = true)
    @PutMapping("/{id}/like")
    public ResponseEntity<Integer> like(@PathVariable Long id) {
        commentService.likeComment(id);
        return ResponseEntity.ok(1);
    }

    @Permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        QueryWrapper<ArticleComment> updateWrapper = new QueryWrapper<>();
        updateWrapper.setEntity(ArticleComment.builder().id(id).build());
        return ResponseEntity.ok(commentMapper.deleteById(id));
    }


}
