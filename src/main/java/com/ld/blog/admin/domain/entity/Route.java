package com.ld.blog.admin.domain.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ld.blog.domain.entity.AuditDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * (Route)实体类
 *
 * @author liudong
 * @since 2022-03-23 23:14:35
 */


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Route extends AuditDomain implements Serializable {
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_ROLES = "roles";
    public static final String FIELD_REDIRECT = "redirect";
    public static final String FIELD_COMPONENT = "component";
    public static final String FIELD_HIDDEN = "hidden";
    public static final String FIELD_ALWAYS_SHOW = "always_show";
    public static final String FIELD_ICON = "icon";
    public static final String FIELD_CREATE_DATE = "createDate";
    public static final String FIELD_LAST_UPDATED_DATE = "lastUpdatedDate";
    public static final String FIELD_CREATE_BY = "createBy";
    private static final long serialVersionUID = -91800942455882470L;
    private Long id;

    private String name;

    private String title;

    private String path;

    private String roles;

    private String redirect;

    private String component;

    private Boolean hidden;

    private Boolean alwaysShow;

    private String icon;

    private Boolean affix;

    private Boolean noCache;

    private Long parentId;

    private String parentIdPath;

    @TableField(exist = false)
    private List<Route> children;

    @TableField(exist = false)
    private Mata meta;

    @TableField(exist = false)
    private Long key;

    public boolean hasRole(Long roleId) {
        if (StringUtils.isBlank(roles) || roleId == null) {
            return false;
        }
        return Arrays.stream(roles.split(",")).collect(Collectors.toList()).contains(roleId.toString());
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Mata implements Serializable {
        private List<String> roles;
        private String icon;
        private Boolean affix;
        private Boolean noCache;
        private String title;
    }
}
