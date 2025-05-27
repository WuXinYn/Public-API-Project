package com.wxy.api.common.model.enums;

public enum PayEnums {

    ALI_PAY(1, "支付宝支付"),
    NORMAL(1, "生效"),
    NOT_NORMAL(0, "未生效"),
    SUCCESS(0, "SUCCESS"),
    FAILURE(1, "FAILURE"),
    WECHAT_PAY(0, "微信支付"),
    FAST_INSTANT_TRADE_PAY(200, "FAST_INSTANT_TRADE_PAY"),
    FAST_INSTANT_TRADE_REFUND(201, "FAST_INSTANT_TRADE_REFUND");

    private final Integer code;
    private final String message;

    PayEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
