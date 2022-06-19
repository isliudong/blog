package com.ld.blog.api.controller.v1;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.ArticlePub;
import com.ld.blog.api.controller.v1.dto.ArchiveDTO;
import com.ld.blog.api.controller.v1.dto.ArticleQueryParam;
import com.ld.blog.app.service.ArticleService;
import com.ld.blog.core.security.core.UserContext;
import com.ld.blog.core.security.permission.Permission;
import com.ld.blog.domain.entity.Article;
import com.ld.blog.infra.mapper.ArticleMapper;
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
 * The type Article controller.
 *
 * @author liudong 2022-06-11 18:52:26
 */
@RestController
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController {

    ArticleMapper articleMapper;
    ArticleService articleService;
    UserContext userContext;


    @Permission(permissionPublic = true)
    @GetMapping
    public ResponseEntity<Page<Article>> getList(Page<Article> page,
                                                 ArticleQueryParam param) {
        if ("+id".equals(param.getSort())) {
            page.addOrder(OrderItem.asc("id"));
        } else {
            page.addOrder(OrderItem.desc("id"));
        }
        return ResponseEntity.ok(articleService.selectList(page, param, null));
    }

    @Permission(permissionPublic = true)
    @GetMapping("/hot")
    public ResponseEntity<Page<Article>> getHotList(Page<Article> page,
                                                    ArticleQueryParam param) {
        page.addOrder(OrderItem.desc("likes"));
        page.addOrder(OrderItem.desc("comments"));
        page.addOrder(OrderItem.desc("views"));
        return ResponseEntity.ok(articleService.selectList(page, param, null));
    }

    @Permission(permissionPublic = true)
    @GetMapping("/published")
    public ResponseEntity<Page<ArticlePub>> getPublishedList(Page<ArticlePub> page, ArticleQueryParam param) {

        return ResponseEntity.ok(articleService.selectPubList(page, param));
    }

    @GetMapping("/archives")
    public ResponseEntity<List<ArchiveDTO>> getList(ArchiveDTO archiveDTO) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        List<Article> articles = articleMapper.selectList(queryWrapper);
        Calendar calendar = Calendar.getInstance();
        Map<Integer, List<Article>> collect = articles.stream()
                .collect(Collectors.groupingBy(article -> {
                    calendar.setTime(article.getCreateDate());
                    return calendar.get(Calendar.YEAR);
                }));
        ArrayList<ArchiveDTO> archiveDTOS = new ArrayList<>();

        collect.forEach((key, value) -> {
            ArchiveDTO archiveDTO1 = new ArchiveDTO();
            archiveDTO1.setYear((long) key);
            archiveDTO1.setArticles(value);
            archiveDTOS.add(archiveDTO1);
        });

        return ResponseEntity.ok(archiveDTOS);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Article> detail(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.detail(id));
    }

    @Permission(permissionLogin = true)
    @PostMapping
    public ResponseEntity<Article> save(@RequestBody @Validated Article article) {
        articleService.insert(article);
        return ResponseEntity.ok(article);
    }

    @Permission(permissionLogin = true)
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable Long id, @RequestBody @Validated Article article) {
        Article article1 = articleMapper.selectById(id);
        userContext.validUser(article1.getCreateBy());
        article.setId(id);
        articleService.update(article);
        return ResponseEntity.ok(1);
    }

    @Permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        userContext.validUser(article.getCreateBy());
        articleService.delete(id);
        return ResponseEntity.ok(1);
    }

    @Permission(permissionLogin = true)
    @PutMapping("/{id}/like")
    public ResponseEntity<Integer> like(@PathVariable Long id) {
        articleService.likeArticle(id);
        return ResponseEntity.ok(1);
    }

    @Permission(permissionLogin = true)
    @PutMapping("/{id}/cancelLike")
    public ResponseEntity<Integer> cancelLike(@PathVariable Long id) {
        articleService.cancelLikeArticle(id);
        return ResponseEntity.ok(1);
    }


    /**
     * 增加文章浏览量
     *
     * @param id 文章id
     * @return ok
     */
    @Permission(permissionPublic = true)
    @PutMapping("/{id}/view")
    public ResponseEntity<Integer> view(@PathVariable Long id) {
        articleService.viewArticle(id);
        return ResponseEntity.ok(1);
    }


}
