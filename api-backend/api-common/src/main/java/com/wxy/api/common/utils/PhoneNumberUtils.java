package com.wxy.api.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberUtils
{
    // 中国大陆手机号正则表达式
    private static final String PHONE_NUMBER_REGEX = "^1[3-9]\\d{9}$";

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
