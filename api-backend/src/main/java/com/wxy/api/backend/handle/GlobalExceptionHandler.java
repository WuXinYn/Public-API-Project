package com.wxy.api.backend.handle;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.wxy.api.common.common.BaseResponse;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import java.net.ConnectException;
import java.util.List;

/**
 * 全局异常处理器
 * 1.捕获代码中的所有异常，内部消化，集中处理，让前端得到更详细的业务报错 2.同时屏蔽掉项目框架本身的异常（不暴露服务器本身状态）3.集中处理（记录日志）
 * 优先级由异常类型的匹配精度决定
 */
@RestControllerAdvice //springAOP
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ConnectException.class)
    public BaseResponse<?> connectExceptionHandler(ConnectException e) {
        log.error("ConnectException: {}", e.getMessage(), e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException: {}", e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public BaseResponse<?> httpClientErrorHandler(HttpClientErrorException e) {
        // 解析网关返回的响应
        String responseBody = e.getResponseBodyAsString();
        HttpStatus statusCode = e.getStatusCode();
        log.error("Exception From GateWay: {}", e.getMessage());
        return ResultUtils.error(statusCode.value(), statusCode.getReasonPhrase());
    }

    /**
     * 处理@Validated参数校验失败异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> exceptionHandler(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                log.warn("Bad Request Parameters: dto entity [{}],field [{}],message [{}]", fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
                stringBuilder.append(fieldError.getDefaultMessage());
            });
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, stringBuilder.toString());
    }

    /**
     * 捕获 MyBatis-Plus 异常
     */
    @ExceptionHandler(MybatisPlusException.class)
    public BaseResponse<?> mybatisPlusExceptionHandler(MybatisPlusException e) {
        log.error("MyBatis-Plus 异常: {}", e.getMessage(), e);
        return ResultUtils.error(ErrorCode.SQL_OPERATION_ERROR, "数据库操作失败");
    }

    /**
     * 捕获数据完整性冲突（如外键约束、非空约束）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public BaseResponse<?> dataIntegrityViolationHandler(DataIntegrityViolationException e) {
        log.error("数据完整性冲突: {}", e.getMessage(), e);
        return ResultUtils.error(ErrorCode.DATA_INTEGRITY_ERROR, "数据约束冲突");
    }

    /**
     * 捕获 SQL 语法错误
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public BaseResponse<?> badSqlGrammarHandler(BadSqlGrammarException e) {
        log.error("SQL 语法错误: {}", e.getMessage(), e);
        return ResultUtils.error(ErrorCode.SQL_SYNTAX_ERROR, "SQL 执行失败");
    }

    /**
     * 捕获数据库访问异常（通用）
     */
    @ExceptionHandler(DataAccessException.class)
    public BaseResponse<?> dataAccessExceptionHandler(DataAccessException e) {
        log.error("数据库访问异常: {}", e.getMessage(), e);
        return ResultUtils.error(ErrorCode.DATABASE_ACCESS_ERROR, "数据库服务异常");
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> exceptionHandler(Exception e) {
        log.error("Exception y: {}", e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }
}
