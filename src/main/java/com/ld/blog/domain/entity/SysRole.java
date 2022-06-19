package com.ld.blog.domain.entity;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ld.blog.admin.domain.entity.Route;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sys_role
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class SysRole extends AuditDomain implements Serializable {
    public static final String ID = "id";
    public static final String ADMIN = "admin";
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    /**
     * 是否可用,1：可用，0不可用
     */
    private Boolean available;
    @TableField(exist = false)
    private List<Route> routes;

    public boolean isAdmin() {
        return SysRole.ADMIN.equals(this.name);
    }
}
