package com.ld.blog.domain.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * tag
 *
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class Tag extends AuditDomain implements Serializable {


    public static final String FILED_NAME = "name";
    private static final long serialVersionUID = 1L;
    private Long id;
    @Length(max = 10)
    private String name;
}
