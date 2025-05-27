package com.wxy.api.common.model.enums;

public enum OrderEnums
{
    TO_BE_PAID(0, "待支付"),
    IS_NORMAL(0, "生效"),
    PAID_ED(1, "已支付"),
    NOT_NORMAL(1, "未生效"),
    CANCELED(2, "已取消"),
    PAY_FAILED(3, "支付失败"),
    REFUNDED(4, "已退款"),
    ORDER_STATUS_PENDING(4, "PENDING"),
    ORDER_STATUS_PAID(5, "PAID"),
    ORDER_STATUS_FAILED(6, "FAILED"),
    ORDER_PAYMENT_STATUS_UNPAID(7, "UNPAID"),
    ORDER_PAYMENT_STATUS_SUCCESS(8, "TRADE_SUCCESS"),
    ORDER_PAYMENT_STATUS_FAILED(9, "FAILED");

    private final Integer code;
    private final String info;

    OrderEnums(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getInfo(){
        return this.info;
    }

    public Integer getCode() {
        return this.code;
    }
}
