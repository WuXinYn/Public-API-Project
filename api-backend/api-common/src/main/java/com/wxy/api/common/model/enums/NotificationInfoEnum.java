package com.wxy.api.common.model.enums;

public enum NotificationInfoEnum
{
    Status_Unread("unread", "未读"),
    Status_Read("read", "已读"),
    Status_Treatment("treatment", "处理中"),
    Timeout("timeout", "失效"),
    Resolved("resolved", "已解决"),
    Status_Error("error", "重大错误"),
    Status_Warning("warning", "警告，处理时长已经超过24h"),
    Type_System("system", "系统通知"),
    Type_User("user", "用户通知"),
    Type_Warning("warning", "警告"),
    Type_Error("error", "重大错误");
    private final String value;
    private final String text;
    NotificationInfoEnum( String value, String text){
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
