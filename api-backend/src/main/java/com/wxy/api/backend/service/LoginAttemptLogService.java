package com.wxy.api.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.api.common.model.entity.LoginAttemptLog;

/**
* @author DELL
* &#064;description  针对表【login_attempt_log】的数据库操作Service
* &#064;createDate  2024-12-25 17:23:26
 */
public interface LoginAttemptLogService extends IService<LoginAttemptLog> {

    /**
     * 检查账号是否被锁定
     *
     * @param userAccount 用户账号
     * @return 账号被锁定-True，否则False
     */
    boolean isAccountLocked(String userAccount);

    /**
     * 检查IP是否被锁定
     *
     * @param ipAddress ip地址
     * @return ip被锁定-True，否则False
     */
    public boolean isIpBlocked(String ipAddress);

    /**
     * 增加账号/IP登录次数
     *
     * @param userAccount 用户账号
     * @param ipAddress ip地址
     */
    int incrementAttempts(String userAccount, String ipAddress);

    /**
     * 重置登录计数
     *
     * @param userAccount 用户账号
     * @param ipAddress ip地址
     */
    void resetAttempts(String userAccount, String ipAddress);

    /**
     * IP查询封禁剩余时间
     *
     * @param ipAddress ip地址
     * @return 剩余时间（min）
     */
    public long checkLeftLimitTime(String ipAddress);
}
