package com.wxy.api.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 订单编号生成工具
 */
public class OrderNumberGenerator
{

    private static final String ORDER_NUMBER_PREFIX = "API";
    private static final String ORDER_NUMBER_SUFFIX = "-";

    /**
     * 盐值+时间戳+UUID+自增ID
     * @param id
     * @return
     */
    public static String generateOrderNumber(long id) {
        // 获取当前时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date()); // 格式化当前时间为日期字符串
        String uuid = UUID.randomUUID().toString().substring(0, 16).replaceAll("[^0-9]", "");
        // 订单编号格式：日期 + 自增ID
        return ORDER_NUMBER_PREFIX + ORDER_NUMBER_SUFFIX + dateStr + ORDER_NUMBER_SUFFIX + uuid + ORDER_NUMBER_SUFFIX + String.format("%06d", id);
    }

    /**
     * 时间戳+UUID
     * @return
     */
    public static String generateOrderNumber(String uuid) {
        // 获取当前时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date()); // 格式化当前时间为日期字符串

        // 使用 UUID 的简短形式作为订单号的一部分，保证唯一性
        String uid = uuid.replace("-", "").substring(0, 8);

        // 订单编号格式：日期 + UUID
        return dateStr + uid;
    }

    /**
     * 时间戳+用户ID+自增ID
     * @param userId
     * @param orderId
     * @return
     */
    public static String generateOrderNumber(long userId, long orderId) {
        // 获取当前时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());

        // 订单编号格式：日期 + 用户ID + 订单ID
        return dateStr + userId + String.format("%06d", orderId);
    }

    public static void main(String[] args) {
        System.out.println(generateOrderNumber(1));
    }
}
