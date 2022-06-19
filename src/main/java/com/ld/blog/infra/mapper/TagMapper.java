package com.ld.blog.infra.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.blog.domain.entity.Tag;

/**
 * @author liudong
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询文章标签
     *
     * @param id 文章id
     * @return 文章标签
     */
    List<Tag> selectArticleTags(Long id);
}
