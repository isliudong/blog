package com.ld.blog.core.config;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ld.blog.core.security.core.UserContext;
import com.ld.blog.domain.entity.SysUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 实体表自动更新策略
 *
 * @author liudong
 */
@Component
public class IMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    @Lazy
    private UserContext userContext;

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createDate", Date.class, new Date());
        this.strictInsertFill(metaObject, "lastUpdatedDate", Date.class, new Date());
        SysUser currentUser = userContext.getCurrentUser();
        this.strictInsertFill(metaObject, "createBy", Long.class, currentUser == null ? -1 : currentUser.getId());
        this.strictUpdateFill(metaObject, "lastUpdatedBy", Long.class, currentUser == null ? -1 : currentUser.getId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        SysUser currentUser = userContext.getCurrentUser();
        this.strictUpdateFill(metaObject, "lastUpdatedDate", Date.class, new Date());
        this.strictUpdateFill(metaObject, "lastUpdatedBy", Long.class, currentUser == null ? -1 : currentUser.getId());
    }

    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        // 原方法，当填充字段不是null时，不会进行填充。即前端更新时携带了旧的update信息，就不会填充update
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }
}
