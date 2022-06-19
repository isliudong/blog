package com.ld.blog.admin.infra.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.blog.admin.domain.entity.ArticlePub;

/**
 * (ArticlePub)应用服务
 *
 * @author liudong
 * @since 2022-03-23 23:14:37
 */
public interface ArticlePubMapper extends BaseMapper<ArticlePub> {
    /**
     * 基础查询
     *
     * @param article 查询条件
     * @return 返回值
     */
    List<ArticlePub> selectList(ArticlePub article);
}

