package com.wxy.api.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.api.backend.mapper.LoginAttemptLogMapper;
import com.wxy.api.backend.service.LoginAttemptLogService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.LoginAttemptLog;
import com.wxy.api.common.model.enums.Limit;
import com.wxy.api.common.service.InnerRedisService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author DELL
* @description 针对表【login_attempt_log(账号IP封禁信息)】的数据库操作Service实现
* @createDate 2024-12-25 17:23:26
*/
@Service
public class LoginAttemptLogServiceImpl extends ServiceImpl<LoginAttemptLogMapper, LoginAttemptLog>
        implements LoginAttemptLogService
{

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 检查账号是否被锁定
     *
     * @param userAccount 用户账号
     * @return 账号被锁定-True，否则False
     */
    @Override
    public boolean isAccountLocked(String userAccount) {
        QueryWrapper<LoginAttemptLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        List<LoginAttemptLog> loginAttemptLog = this.list(queryWrapper);
        if (loginAttemptLog.isEmpty()) {
            return false;
        }
        Date lockTime = loginAttemptLog.get(0).getAttempt_time();
        if (lockTime != null) {
            long elapsedMinutes = (System.currentTimeMillis() - lockTime.getTime()) / (1000 * 60);
            if (elapsedMinutes > Limit.LOCK_DURATION_MINUTES.getValue()) {
                // 解锁账号
                boolean remove = this.remove(queryWrapper);
                if(!remove) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号解封失败！");
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 检查IP是否被锁定
     *
     * @param ipAddress ip地址
     * @return ip被锁定-True，否则False
     */
    @Override
    public boolean isIpBlocked(String ipAddress) {
        String key = Limit.IP_Redis_Address.getAddress() + ipAddress;
        Object redisGetKey = redisTemplate.opsForValue().get(key);
        if(redisGetKey == null) {
            return false;
        }
        int attempts = Integer.parseInt((String) redisGetKey);
        return attempts >= Limit.IP_MAX_ATTEMPTS.getValue();
    }

    /**
     * 增加账号/IP登录次数
     *
     * @param userAccount 用户账号
     * @param ipAddress ip地址
     */
    @Override
    public int incrementAttempts(String userAccount, String ipAddress) {
        String userKey = Limit.UserAccount_Redis_Address.getAddress() + userAccount;
        String ipKey = Limit.IP_Redis_Address.getAddress()+ ipAddress;

        Object redisGetUserKey = redisTemplate.opsForValue().get(userKey);
        Object redisGetIPKey = redisTemplate.opsForValue().get(ipKey);

        int userAttempts = 1;
        int ipAttempts = 1;
        boolean userSign = false;
        boolean ipSign = false;

        if (redisGetUserKey == null) {
            redisTemplate.opsForValue().set(userKey, userAttempts, 10, TimeUnit.MINUTES);
            userSign = true;
        }
        if (redisGetIPKey == null) {
            redisTemplate.opsForValue().set(ipKey,  ipAttempts, 10, TimeUnit.MINUTES);
            ipSign = true;
        }

        // 增加登录次数
        if(!userSign) {
            userAttempts = Integer.parseInt((String) redisGetUserKey) + 1;
            redisTemplate.opsForValue().set(userKey, userAttempts, 10, TimeUnit.MINUTES);
        }
        if(!ipSign){
            ipAttempts =  Integer.parseInt((String) redisGetIPKey) + 1;
            redisTemplate.opsForValue().set(ipKey, ipAttempts, 10, TimeUnit.MINUTES);
        }

        if (userAttempts >= Limit.MAX_ATTEMPTS.getValue()) {
            // 锁定账号
            LoginAttemptLog loginAttemptLog = new LoginAttemptLog();
            loginAttemptLog.setUserAccount(userAccount);
            loginAttemptLog.setIpAddress(ipAddress);
            this.saveOrUpdate(loginAttemptLog);
        }
        return userAttempts;
    }

    /**
     * 重置登录计数
     *
     * @param userAccount 用户账号
     * @param ipAddress ip地址
     */
    @Override
    public void resetAttempts(String userAccount, String ipAddress) {
        redisTemplate.delete(Limit.UserAccount_Redis_Address.getAddress() + userAccount);
        redisTemplate.delete(Limit.IP_Redis_Address.getAddress() + ipAddress);
    }

    /**
     * IP查询封禁剩余时间
     *
     * @param ipAddress ip地址
     * @return 剩余时间（min）
     */
    @Override
//    @Cacheable(value = "ip_left_limit_time")
    public long checkLeftLimitTime(String ipAddress) {
        QueryWrapper<LoginAttemptLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ipAddress", ipAddress);
        List<LoginAttemptLog> list = this.list(queryWrapper);
        if(list != null) {
            return (System.currentTimeMillis() - list.get(0).getAttempt_time().getTime()) / (1000 * 60);
        }
        else {
            return 0;
        }
    }
}




