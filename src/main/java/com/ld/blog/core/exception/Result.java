package com.ld.blog.core.exception;

import lombok.Data;


/**
 * @author liudong
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(T data) {
        this.data = data;
    }

    private Result() {
    }

    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(CommonMessage commonMessage) {
        if (commonMessage != null) {
            this.code = commonMessage.getCode();
            this.msg = commonMessage.getMsg();
        }
    }

    /**
     * 成功时候的调用
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>(CommonMessage.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 成功时候的调用
     */
    public static <T> Result<T> successMsg(String msg) {
        return new Result<T>(200, msg);
    }

    /**
     * 失败时候的调用
     */
    public static <T> Result<T> errorCodeMsg(CommonMessage commonMessage) {
        return new Result<T>(commonMessage);
    }

    /**
     * 成功时候的调用
     */
    public static <T> Result<T> successCodeMsg(CommonMessage commonMessage) {
        return new Result<T>(commonMessage);
    }

    public static <T> Result<T> error(T data) {
        Result<T> result = new Result<T>(CommonMessage.SYSTEM_ERROR);
        result.setData(data);
        return result;
    }

    /**
     * 失败时候的调用
     */
    public static <T> Result<T> errorMsg(String msg) {
        return new Result<T>(0, msg);
    }

    /**
     * 全部参数
     *
     * @param
     * @param <T>
     * @return
     */
    public static <T> Result<T> getResult() {
        return new Result<T>();
    }

    public static <T> Result<T> toAjaxResult(int rows) {
        return rows > 0 ? success(null) : error(null);
    }

    public static <T> Result<T> toAjaxResult(boolean rows) {
        return rows ? success(null) : error(null);
    }

}


