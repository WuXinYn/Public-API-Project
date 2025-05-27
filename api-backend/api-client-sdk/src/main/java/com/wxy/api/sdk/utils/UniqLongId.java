package com.wxy.api.sdk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 生成唯一id
 */
public class UniqLongId
{
    public static long uniqId()
    {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedTimestamp = sdf.format(new Date(timestamp));
        Random random = new Random();
        int randomInt = random.nextInt(1000);
        String idString = formattedTimestamp + String.format("%03d", randomInt);
        return Long.parseLong(idString);
    }
}
