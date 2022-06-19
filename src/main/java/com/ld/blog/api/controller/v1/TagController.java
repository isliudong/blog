package com.ld.blog.api.controller.v1;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.blog.core.security.permission.Permission;
import com.ld.blog.domain.entity.Tag;
import com.ld.blog.infra.mapper.TagMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liudong
 */
@RestController
@RequestMapping("/tags")
@AllArgsConstructor
public class TagController {

    TagMapper tagMapper;


    @GetMapping
    public ResponseEntity<Page<Tag>> getList(Page<Tag> page, Tag tag) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(tag);
        return ResponseEntity.ok(tagMapper.selectPage(page, queryWrapper));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> detail(@PathVariable Long id) {
        return ResponseEntity.ok(tagMapper.selectById(id));
    }

    @Permission(permissionLogin = true)
    @PostMapping
    public ResponseEntity<Tag> insert(@RequestBody @Validated Tag tag) {
        tagMapper.insert(tag);
        return ResponseEntity.ok(tag);
    }

    @Permission
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable Long id, @RequestBody @Validated Tag tag) {
        tag.setId(id);
        return ResponseEntity.ok(tagMapper.updateById(tag));
    }

    @Permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        return ResponseEntity.ok(tagMapper.deleteById(id));
    }


}
