package com.ld.blog.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author liudong
 */
@Data
public class AuditDomain {
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastUpdatedDate;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long lastUpdatedBy;
    //@TableField(fill = FieldFill.INSERT_UPDATE)
    //private Long objectVersionNumber;

}
