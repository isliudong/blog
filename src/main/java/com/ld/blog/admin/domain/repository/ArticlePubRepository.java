package com.ld.blog.admin.domain.repository;

import java.util.List;

import com.ld.blog.admin.domain.entity.ArticlePub;

/**
 * (ArticlePub)资源库
 *
 * @author liudong
 * @since 2022-03-23 23:14:38
 */
public interface ArticlePubRepository {
    /**
     * 查询
     *
     * @param articlePub 查询条件
     * @return 返回值
     */
    List<ArticlePub> selectList(ArticlePub articlePub);

    /**
     * 根据主键查询（可关联表）
     *
     * @param id 主键
     * @return 返回值
     */
    ArticlePub selectByPrimary(Long id);

    void deleteByPrimaryKey(ArticlePub articlePub);
}
