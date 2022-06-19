package com.ld.blog.admin.api.controller.dto;

import java.util.List;

import com.ld.blog.admin.domain.entity.Route.Mata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liudong
 * @since 2022-03-27 00:31:58
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {


    private Long id;

    private String name;

    private String title;

    private String path;

    private String roles;

    private String redirect;

    //private String component;

    private Boolean hidden;

    private Boolean alwaysShow;

    private String icon;

    private Boolean affix;

    private Boolean noCache;

    private Long parentId;

    private String parentIdPath;

    private List<RouteDTO> children;

    private Mata meta;
}
