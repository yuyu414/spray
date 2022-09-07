package com.no.hurdles.spray.controller;

import com.no.hurdles.spray.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 基础异常
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResult runtimeExceptionHander(RuntimeException e) {
        log.error(e.getMessage(), e);
        return BaseResult.fail(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(Exception.class)
    public BaseResult exceptionHander(Exception e) {
        log.error(e.getMessage(), e);
        return BaseResult.fail(e.getMessage());
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Throwable.class)
    public BaseResult otherException(Throwable t) {
        log.error(t.getMessage(), t);
        return BaseResult.fail(t.getMessage());
    }

}
