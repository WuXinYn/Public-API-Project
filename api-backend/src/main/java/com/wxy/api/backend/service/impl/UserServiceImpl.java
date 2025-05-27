package com.wxy.api.backend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.api.backend.mapper.UserMapper;
import com.wxy.api.backend.service.LoginAttemptLogService;
import com.wxy.api.backend.service.NotificationInfoService;
import com.wxy.api.backend.service.UserService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.NotificationInfo;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.enums.Limit;
import com.wxy.api.common.model.vo.UserVO;
import com.wxy.api.common.service.InnerRedisService;
import com.wxy.api.common.utils.PhoneNumberUtils;
import com.wxy.api.sdk.utils.CusAccessObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import static com.wxy.api.common.constant.UserConstant.ADMIN_ROLE;
import static com.wxy.api.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{

    @Resource
    private UserMapper userMapper;

    @Resource
    private LoginAttemptLogService loginAttemptLogService;

    @Resource
    private NotificationInfoService notificationInfoService;

    @DubboReference
    private InnerRedisService innerRedisService;

    /**
     * 盐值，混淆密码
     *
     */
    private static final String salt = Limit.SALT.getAddress();

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 查询数据库
        synchronized (userAccount.intern()) {
            // 账户不能重复
            long count = userMapper.countAllUsers(userAccount);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册！");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
            // 3. 分配accessKey,secretKey
            String accessKey = DigestUtil.md5Hex(salt + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(salt + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserName(userAccount);
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            else {
                // 欢迎通知
                NotificationInfo notificationInfo = notificationInfoService.generateWelcomeNotices(user.getId());
                notificationInfoService.save(notificationInfo);
            }
            return user.getId();
        }
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 登录用户
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        String userLoginIP = CusAccessObjectUtil.getIpAddress(request);
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号格式错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
        }
        // 2. 限流，记录这个账号登录过多少次，单个ip固定时间登录太多次进行限流、封号等
        if (loginAttemptLogService.isAccountLocked(userAccount)) {
            throw new BusinessException(ErrorCode.Account_blocked_ERROR, "账号已被封禁");
        }
        if (loginAttemptLogService.isIpBlocked(userLoginIP)) {
            throw new BusinessException(ErrorCode.IP_Restricted_ERROR, "IP 被限流");
        }
        // 3. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
        // 验证登录，查看用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            // 增加登录失败计数
            int userAttempts = loginAttemptLogService.incrementAttempts(userAccount, userLoginIP);
            int leftUserAttempts = Limit.MAX_ATTEMPTS.getValue() - userAttempts;
            log.info("user login failed, userAccount cannot match userPassword");
            String msg = "用户不存在或密码错误, 你已经试错了"+userAttempts+"次，你还剩"+leftUserAttempts+"次机会！";
            String msgT = "";
            if (leftUserAttempts <= 0) {
                long leftTime = loginAttemptLogService.checkLeftLimitTime(userLoginIP);
                msgT = "次数已耗尽，您的IP将被封禁！剩余" + leftTime + "分钟";
            }
            throw new BusinessException(ErrorCode.PARAMS_ERROR, msg + msgT);
        }
        // 脱敏
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    @Override
//    @Cacheable(value = "login_user", key = "#request.getSession().getAttribute(\"userLoginState\")")
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 从数据库查询（适用用户数据更新频繁；否则追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        User user = this.getById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return 是管理员-True，否则False
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO user = (UserVO) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 成功-True，否则抛异常
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 自定义增加用户
     */
    @Override
    public boolean addUser(User user){
        // 查询数据库
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        synchronized (userAccount.intern()) {
            // 账户不能重复
            long count = userMapper.countAllUsers(userAccount);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册！");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
            // 3. 分配accessKey,secretKey
            String accessKey = DigestUtil.md5Hex(salt + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(salt + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            else {
                // 欢迎通知
                NotificationInfo notificationInfo = notificationInfoService.generateWelcomeNotices(user.getId());
                notificationInfoService.save(notificationInfo);
            }
            return user.getId() != 0;
        }
    }

    /**
     * 更新用户信息
     */
    @Override
    public boolean updateUser(User user){
        // 1. 校验
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 查询数据库
        String userAccount = user.getUserAccount();
        long userId = user.getId();
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            queryWrapper.ne("id",userId);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册！");
            }
            return this.updateById(user);
        }
    }

    /**
     * 校验sms验证码
     */
    @Override
    public void validateLoginCaptcha(String userAccount, String code)
    {
        if (StringUtils.isAnyBlank(userAccount, code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号、验证码不能为空");
        }
        if (!PhoneNumberUtils.isValidPhoneNumber(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }
        String accountKey = Limit.CAPTCHA_CODE_KEY.getAddress() + userAccount;
        String redisCode = innerRedisService.findParam(accountKey);
        if (redisCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已过期");
        }
        if (!redisCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
    }
}




