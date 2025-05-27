package com.wxy.api.sdk.utils;

import java.net.InetAddress;

/**
 * IP地址转换
 */
public class IPConversion
{
    /**
     * IPv4 转 IPv6
     *
     * @param ipv4
     * @return
     * @throws Exception
     */
    public static String convertIPv4ToIPv6(String ipv4) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(ipv4);
        if (inetAddress instanceof java.net.Inet4Address) {
            // 转换为 IPv4-mapped IPv6 地址
            return "::ffff:" + ipv4;
        }
        return ipv4;
    }

    /**
     * 将 IPv4-mapped IPv6 地址转换回 IPv4
     *
     * @param ipv6
     * @return
     * @throws Exception
     */
    public static String convertIPv6ToIPv4(String ipv6) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(ipv6);
        if (inetAddress.isLoopbackAddress()) {
            // 如果是回环地址，统一返回为 IPv4 格式
            return "127.0.0.1";
        }
        return inetAddress.getHostAddress(); // 否则返回标准地址
    }
}
