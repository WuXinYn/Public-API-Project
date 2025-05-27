package com.wxy.api.common.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonUtils
{
    /**
     * 将对象转换为JSON字符串
     */
    public static String parseObjectToJsonString(Object params)
    {
        // 创建 Gson 实例
        Gson gson = new Gson();
        // 将对象转换为 JSON 字符串
        return  gson.toJson(params);
    }

    /**
     * 将JSON字符串转换为指定类型的对象。
     * @param data 要解析的json字符串
     * @param clazz 指定的类型
     */
    public static <T> T parseJsonToObject(String data, Class<T> clazz){

        // 创建 Gson 实例
        Gson gson = new Gson();

        // 定义目标类型
        Type type = TypeToken.getParameterized(clazz).getType();

        // 使用 Gson 将 JSON 字符串转换为指定类型的列表
        return gson.fromJson(data, type);
    }
}
