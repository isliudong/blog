package com.ld.blog.admin.app.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.admin.domain.entity.ArticlePub;

/**
 * (ArticlePub)应用服务
 *
 * @author liudong
 * @since 2022-03-23 23:14:38
 */
public interface ArticlePubService {

    /**
     * 查询数据
     *
     * @param pageRequest 分页参数
     * @param articlePubs 查询条件
     * @return 返回值
     */
    Page<ArticlePub> selectList(Page<ArticlePub> pageRequest, ArticlePub articlePubs);

    /**
     * 保存数据
     *
     * @param articlePubs 数据
     */
    void saveData(List<ArticlePub> articlePubs);

}

