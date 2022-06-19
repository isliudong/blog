package com.ld.blog.domain.entity;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * sys_user
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysUser extends AuditDomain implements Serializable {
    public static final String FILED_USERNAME = "username";
    public static final String FILED_USEREMAIL = "email";
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 账号
     */
    @Length(max = 30, groups = {Register.class, Update.class})
    private String usercode;
    /**
     * 姓名
     */
    @Length(max = 30, groups = {Register.class, Update.class})
    @NotBlank(groups = {Register.class})
    private String username;
    /**
     * 密码
     */
    @Length(max = 30, groups = {Register.class})
    @NotBlank(groups = {Register.class})
    private String password;
    /**
     * 盐
     */
    private String salt;
    /**
     * 账号是否锁定，1：锁定，0未锁定
     */
    private Boolean locked;
    @TableField(exist = false)
    private Boolean author = false;
    @Length(max = 1000, groups = {Register.class, Update.class})
    private String avatar;
    @NotBlank
    private String email;
    @TableField(exist = false)
    private List<SysRole> roles;

    public interface Register {
    }

    public interface Update {
    }
}
