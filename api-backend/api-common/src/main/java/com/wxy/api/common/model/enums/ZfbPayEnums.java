package com.wxy.api.common.model.enums;

public enum ZfbPayEnums
{
    ORDER_STATUS_PENDING("PENDING"),
    ORDER_STATUS_PAID("PAID"),
    ORDER_STATUS_FAILED("FAILED"),
    ORDER_PAYMENT_STATUS_UNPAID("UNPAID"),
    ORDER_PAYMENT_STATUS_SUCCESS("TRADE_SUCCESS"),
    ORDER_PAYMENT_STATUS_FAILED("FAILED");

    private final String info;

    ZfbPayEnums(String info)
    {
        this.info = info;
    }

    public String getInfo(){
        return this.info;
    }
}
