package com.wxy.api.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.vo.UserVO;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 请求
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return 是管理员-True，否则False
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 成功-True，否则抛异常
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 自定义增加用户
     */
    boolean addUser(User user);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 校验sms验证码
     */
    void validateLoginCaptcha(String userAccount, String code);
}
