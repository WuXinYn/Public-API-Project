package com.wxy.api.common.service;

/**
 * redis Service
 */
public interface InnerRedisService
{
    /**
     * 获取锁
     *
     * @param interfaceInfoId
     */
    void tryLock(long interfaceInfoId, long userId, String lockValue);

    /**
     * 释放锁
     */
    void releaseLock(long interfaceInfoId, long userId, String lockValue);

    /**
     * 查找参数
     */
    String findParam(String key);

    /**
     * 删除参数
     *
     * @param key 键
     */
    void deleteParams(String key);

    /**
     * 查找参数(随机数)
     */
    boolean findParams(String key, Object nonce);

    /**
     * 存储参数
     */
    void setParams(String key, Object params);

    /**
     * 存储参数,并设置时间
     *
     * @param key 键
     * @param params 值
     * @param time 存活时间
     */
    void setParams(String key, Object params,long time);

    /**
     * 检查IP是否正常
     */
    boolean checkIPBlocked(String ipAddress);

    /**
     * IP调用次数+1
     */
    void addIPInvokeNumber(String ipAddress);

}
