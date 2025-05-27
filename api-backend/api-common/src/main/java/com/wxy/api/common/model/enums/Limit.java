package com.wxy.api.common.model.enums;

public enum Limit
{
    MAX_ATTEMPTS(5, "5"),
    IP_MAX_ATTEMPTS(20, "20"),
    IP_Redis_Address(10086,"login:attempts:ip:"),
    API_Kill_Address(30,"api:kill:ip:"),
    UserAccount_Redis_Address(10087,"login:attempts:userAccount:"),
    SALT(15, "wxy"),
    /**
     * 验证码 redis key
     */
    CAPTCHA_CODE_KEY(11,"captcha_codes:"),
    LOCK_DURATION_MINUTES(10, "10");

    private final int value;

    private final String address;

    Limit(int value, String address) {
        this.value = value;
        this.address = address;
    }

    public int getValue() {
        return value;
    }

    public String getAddress() {
        return address;
    }
}
