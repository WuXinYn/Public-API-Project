package com.wxy.api.backend.service.impl.inner;

import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.enums.Limit;
import com.wxy.api.common.service.InnerRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Redis Service
 *
 */
@DubboService
@Slf4j
public class InnerRedisServiceImpl implements InnerRedisService
{
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_KEY_PREFIX = "lock:api:";

    /**
     * 获取锁
     *
     * @param interfaceInfoId 接口id
     */
    @Override
    public void tryLock(long interfaceInfoId, long userId, String lockValue) {
        String lockKey = LOCK_KEY_PREFIX + interfaceInfoId + ":" + userId;
        // 尝试获取锁
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lockAcquired)) {
            log.warn("Failed to acquire lock for API ID: {}", interfaceInfoId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试");
        }
    }

    /**
     * 释放锁
     *
     * @param interfaceInfoId 接口id
     */
    @Override
    public void releaseLock(long interfaceInfoId, long userId, String lockValue) {
        String lockKey = LOCK_KEY_PREFIX + interfaceInfoId + ":" + userId;
        String currentLockValue = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(currentLockValue)) {
            redisTemplate.delete(lockKey); // 只有是自己的锁才删除
        }
    }

    /**
     * 查找参数
     *
     * @param key 键
     * @return 找到-True，否则-False
     */
    @Override
    public String findParam(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 查找参数(随机数)
     *
     * @param key 键
     * @param nonce 随机数
     * @return 成功-True，失败-False
     */
    @Override
    public boolean findParams(String key, Object nonce){
//        String s = redisTemplate.opsForValue().getAndDelete(key); // 获取并删除键对应的值
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            redisTemplate.delete(key);
        }
        else {
            return false;
        }
        String requestNonce = (String) nonce;
        return requestNonce.equals(value);
    }

    /**
     * 存储参数(随机数)
     *
     * @param key 键
     * @param params 值
     */
    @Override
    public void setParams(String key, Object params) {
        String setParams = String.valueOf(params);
        redisTemplate.opsForValue().set(key, setParams, 5, TimeUnit.MINUTES);
    }


    /**
     * 存储参数,并设置时间
     *
     * @param key 键
     * @param params 值
     */
    @Override
    public void setParams(String key, Object params,long time) {
        String setParams = String.valueOf(params);
        redisTemplate.opsForValue().set(key, setParams, time, TimeUnit.MINUTES);
    }

    /**
     * 删除参数
     *
     * @param key 键
     */
    @Override
    public void deleteParams(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }

    /**
     * 检查IP是否正常
     *
     * @param ipAddress IP地址
     */
    @Override
    public boolean checkIPBlocked(String ipAddress) {
        String key = Limit.IP_Redis_Address.getAddress()+ ipAddress;
        String ipInvokeNumber = redisTemplate.opsForValue().get(key);
        if(ipInvokeNumber == null) {
            return false;
        }
        else {
            int number = Integer.parseInt(ipInvokeNumber);
            return number >= 20;
        }
    }

    /**
     * IP调用次数+1
     *
     * @param ipAddress IP地址
     */
    @Override
    public void addIPInvokeNumber(String ipAddress) {
        String key = Limit.IP_Redis_Address.getAddress() + ipAddress;
        String ipInvokeNumber = redisTemplate.opsForValue().get(key);
        if(ipInvokeNumber == null){
            redisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        }
        else{
            int newNumber = Integer.parseInt(ipInvokeNumber) + 1;
            redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(newNumber), 10, TimeUnit.SECONDS);
        }
    }
}
