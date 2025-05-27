package com.wxy.api.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxy.api.backend.mapper.UserMapper;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.enums.UserRoleEnum;
import com.wxy.api.common.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户 Service
 *
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService
{

    @Resource
    private UserMapper userMapper;


    /**
     * 获取用户信息
     *
     * @param accessKey 密钥
     * @return 用户
     */
    @Override
//    @Cacheable(value = "invoke_user")
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 获取所有管理员id
     */
    @Override
//    @Cacheable(value = "admin_user")
    public List<User> getAllAdmin(){
        List<User> adminList = new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id")
                    .eq("userRole", UserRoleEnum.Admin.getValue());
        adminList = userMapper.selectList(queryWrapper);
        return adminList;
    }

    /**
     * 获取所有普通用户id
     */
    @Override
//    @Cacheable(value = "common_users")
    public List<User> getAllUsers(){
        List<User> userList = new ArrayList<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id")
                .eq("userRole", UserRoleEnum.User.getValue());
        userList = userMapper.selectList(queryWrapper);
        return userList;
    }
}
