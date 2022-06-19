package com.ld.blog.admin.app.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.app.service.ArticlePubService;
import com.ld.blog.admin.domain.entity.ArticlePub;
import com.ld.blog.admin.domain.repository.ArticlePubRepository;
import com.ld.blog.admin.infra.mapper.ArticlePubMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (ArticlePub)应用服务
 *
 * @author liudong
 * @since 2022-03-23 23:14:38
 */
@Service
public class ArticlePubServiceImpl implements ArticlePubService {
    @Autowired
    private ArticlePubRepository articlePubRepository;
    @Autowired
    private ArticlePubMapper articlePubMapper;

    @Override
    public Page<ArticlePub> selectList(Page<ArticlePub> pageRequest, ArticlePub articlePub) {
        return articlePubMapper.selectPage(pageRequest, new QueryWrapper<>(articlePub));
    }

    @Override
    public void saveData(List<ArticlePub> articlePubs) {
        articlePubs.forEach(item -> {
            if (item.getId() == null) {
                articlePubMapper.insert(item);
            } else {
                articlePubMapper.updateById(item);
            }
        });
    }
}
