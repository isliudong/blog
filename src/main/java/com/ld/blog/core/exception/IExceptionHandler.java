package com.ld.blog.core.exception;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author liudong
 */
@RestControllerAdvice
@Slf4j
public class IExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public Result<Object> jsonErrorHandler(CommonException e) {
        Result<Object> result = Result.getResult();
        result.setMsg(e.getMessage());
        result.setCode(e.getCode());
        result.setData(null);
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> systemJsonErrorHandler(Exception e) {
        e.printStackTrace();
        return Result.errorCodeMsg(CommonMessage.SYSTEM_ERROR);
    }

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> validationBodyException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        String message = "";
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.error("Data check failure : object{"
                        + fieldError.getObjectName()
                        + "},field{" + fieldError.getField()
                        + "},errorMessage{" + fieldError.getDefaultMessage()
                        + "}");
            });
            if (CollectionUtils.isNotEmpty(errors)) {
                FieldError fieldError = (FieldError) errors.get(0);
                message = fieldError.getField() + " " + fieldError.getDefaultMessage();
            }
        }
        return Result.errorMsg(message);
    }


}

