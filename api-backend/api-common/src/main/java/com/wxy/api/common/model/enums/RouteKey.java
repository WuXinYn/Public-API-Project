package com.wxy.api.common.model.enums;

public enum RouteKey
{
    Default("default"),
    Order_Update("order_update"),
    Order_Create("order_create"),
    Order_Cancel("order_cancel"),
    Order_Pay("order_pay"),
    Order_Refund("order_refund"),
    Order_Refund_Complete("order_refund_complete"),
    Order_Refund_Apply("order_refund_apply"),
    WxPay_Notify("wxpay_notify"),
    Sms_Message("sms_message");

    private final String key;

    RouteKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }
}
