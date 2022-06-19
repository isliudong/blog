package com.ld.blog.infra.constants;


import com.ld.blog.core.exception.CommonException;

/**
 * 公共常量
 *
 * @author liudong
 */
public class CommonConst {

    /**
     * 高亮标签
     */
    public static final String PRE_TAG = "<span style='color:#f47466'>";
    /**
     * 高亮标签
     */
    public static final String POST_TAG = "</span>";

    private CommonConst() {
        throw new CommonException("不支持实例化使用方式");
    }


}
