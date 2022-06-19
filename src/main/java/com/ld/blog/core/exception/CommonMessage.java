package com.ld.blog.core.exception;

/**
 * @author liudong
 */
public enum CommonMessage {
    /**
     * success
     */
    SUCCESS("success"),
    /**
     * 系统未知错误
     */
    SYSTEM_ERROR("系统错误,请联系管理员");
    private int code;
    private String msg;

    CommonMessage(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
