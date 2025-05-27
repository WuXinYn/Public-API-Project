package com.wxy.api.common.model.enums;

public enum UserInterfaceInfoEnum
{
    NORMAL(0),
    FORBIDDEN(1);

    private final int value;

    UserInterfaceInfoEnum(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
