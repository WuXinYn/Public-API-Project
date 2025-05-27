package com.wxy.api.common.common;

/**
 * 错误码
 *
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    SEND_SUCCESS(1, "验证码发送成功, 请在5分钟内完成验证"),
    SEND_ERROR(40001, "验证码发送失败"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    Account_blocked_ERROR(40102, "账号被封禁"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    IP_Restricted_ERROR(50002, "IP 被限流"),
    Request_ERROR(50003, "请求失败"),
    SQL_OPERATION_ERROR(50004, "数据库操作失败"),
    DATA_INTEGRITY_ERROR(50005, "数据约束冲突"),
    SQL_SYNTAX_ERROR(50006, "SQL 语法错误"),
    DATABASE_ACCESS_ERROR(50007, "数据库服务异常"),
    ;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
