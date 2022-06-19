package com.ld.blog.infra.repository.impl;

import java.util.List;
import javax.annotation.Resource;

import com.ld.blog.admin.domain.entity.ArticlePub;
import com.ld.blog.admin.infra.mapper.ArticlePubMapper;
import com.ld.blog.domain.repository.ArticleRepository;
import com.ld.blog.infra.mapper.ArticleMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * (ArticlePub)资源库
 *
 * @author liudong
 * @since 2022-03-23 23:14:38
 */
@Component
public class ArticleRepositoryImpl implements ArticleRepository {
    @Resource
    private ArticlePubMapper articlePubMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public List<ArticlePub> selectList(ArticlePub articlePub) {
        return articlePubMapper.selectList(articlePub);
    }

    @Override
    public ArticlePub selectByPrimary(Long id) {
        ArticlePub articlePub = new ArticlePub();
        articlePub.setId(id);
        List<ArticlePub> articlePubs = articlePubMapper.selectList(articlePub);
        if (CollectionUtils.isEmpty(articlePubs)) {
            return null;
        }
        return articlePubs.get(0);
    }

    @Override
    public void deleteByPrimaryKey(Long id) {
        articlePubMapper.deleteById(id);
        articleMapper.deleteById(id);
    }
}
