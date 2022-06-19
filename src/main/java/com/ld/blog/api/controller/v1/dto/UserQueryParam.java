package com.ld.blog.api.controller.v1.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liudong
 * @since 2022-04-03 12:08:50
 */
@Getter
@Setter
public class UserQueryParam {

    private String username;

    private String email;

    private Long locked;

    private String sort;


}
