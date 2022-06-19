package com.ld.blog.app.service;

import com.ld.blog.infra.mapper.TagMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author liudong
 */
@Service
@Slf4j
@AllArgsConstructor
public class TagService {
    private final TagMapper tagMapper;


}
