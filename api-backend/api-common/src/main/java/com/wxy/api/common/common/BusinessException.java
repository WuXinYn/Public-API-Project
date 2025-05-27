package com.wxy.api.common.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 * 支持更多字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private final int code; // 给RuntimeException异常扩充code字段

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
