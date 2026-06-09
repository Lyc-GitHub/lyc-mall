package com.lyc.learn.common.exception;

import com.lyc.learn.common.vo.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;

@RestControllerAdvice
public class MallExceptionHandler {
    
    Logger logger = LoggerFactory.getLogger(MallExceptionHandler.class);
    
    @ExceptionHandler(value = MallException.class)
    public CommonResult handleMallException(MallException e) {
        logger.error(e.getMessage(), e);
        return CommonResult.error(e.getMessage());
    }
    
    @ExceptionHandler(value = Exception.class)
    public CommonResult handleException(Exception e) {
        if (e instanceof UndeclaredThrowableException) {
            Throwable cause = e.getCause();
            logger.error("Unknown Error", cause);
            return CommonResult.error((null == cause) ? "Unknown Error" : cause.toString());
        }
        logger.error(e.getMessage(), e);
        return CommonResult.error(e.getMessage());
    }
    
    @ExceptionHandler(value = Throwable.class)
    public CommonResult handleException(Throwable e) {
        logger.error(e.getMessage(), e);
        return CommonResult.error(e.getMessage());
    }
    
    @ExceptionHandler(value = RuntimeException.class)
    public CommonResult handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage(), e);
        return CommonResult.error(e.getMessage());
    }
    
}
