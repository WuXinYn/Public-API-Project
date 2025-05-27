package com.wxy.api.common.utils;

import java.net.URL;
import java.net.MalformedURLException;

public class UrlValidator {
    public static boolean isValidUrlFormat(String url) {
        try {
            new URL(url); // 尝试创建URL对象
            return true;
        } catch (MalformedURLException e) {
            return false; // URL格式不合法
        }
    }

    public static void main(String[] args) {
        String url = "https://www.example.com";
        System.out.println("URL格式是否合法: " + isValidUrlFormat(url));
    }
}
