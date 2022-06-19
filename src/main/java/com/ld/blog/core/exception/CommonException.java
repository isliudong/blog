package com.ld.blog.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author liudong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonException extends RuntimeException {

    private int code;

    public CommonException(int code) {
        this.code = code;
    }

    public CommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CommonException(CommonMessage commonMessage) {
        super(commonMessage.getMsg());
        this.code = commonMessage.getCode();
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}

