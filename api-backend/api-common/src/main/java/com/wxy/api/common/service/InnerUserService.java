package com.wxy.api.common.service;


import com.wxy.api.common.model.entity.User;

import java.util.List;

/**
 * 用户服务
 *
 */
public interface InnerUserService {

    /**
     * 获取调用的用户信息
     */
    User getInvokeUser(String accessKey);

    /**
     * 获取所有管理员id
     */
    List<User> getAllAdmin();

    /**
     * 获取所有普通用户id
     */
    List<User> getAllUsers();
}
