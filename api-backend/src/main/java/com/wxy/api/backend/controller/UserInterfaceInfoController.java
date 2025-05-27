package com.wxy.api.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.api.backend.annotation.AuthCheck;
import com.wxy.api.backend.mapper.UserInterfaceInfoMapper;
import com.wxy.api.backend.service.UserInterfaceInfoService;
import com.wxy.api.backend.service.UserService;
import com.wxy.api.common.common.*;
import com.wxy.api.common.constant.CommonConstant;
import com.wxy.api.common.constant.UserConstant;
import com.wxy.api.common.model.dto.userinterfaceinfo.*;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户调用接口信息
 *
 * @author wxy
 */
@RestController // 每个方法的返回值都会以 JSON 或 XML 的形式直接写入 HTTP 响应体中，相当于在每个方法上都添加了 @ResponseBody 注解。
@RequestMapping("/userInterfaceInfo")
@Slf4j // 用作日志输出
public class UserInterfaceInfoController
{

    @Resource //byName来实现依赖注入的
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private UserService userService;


    /**
     * 创建
     *
     * @param userInterfaceInfoAddRequest 创建请求
     * @param request 请求
     * @return 通用返回类
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        // 浅拷贝，类中都是单一的属性
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        // 检验登录状态，拿到用户信息
        User loginUser = userService.getLoginUser(request);
        // 系统赋值用户id传参
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建用户调用接口数据事务失败");
        }
        long newInterfaceId = userInterfaceInfo.getId();
        return ResultUtils.success(newInterfaceId);
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求
     * @param request 请求
     * @return 通用返回类
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除用户调用接口数据事务失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新
     *
     * @param userInterfaceInfoUpdateRequest 更新请求
     * @param request 请求
     * @return 通用返回类
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, HttpServletRequest request) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        // 验证登录状态
        User user = userService.getLoginUser(request);
        // 判断是否存在
        long id = userInterfaceInfoUpdateRequest.getId();
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户调用接口数据事务失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取
     *
     * @param id 接口id
     * @return 通用返回类
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        return ResultUtils.success(userInterfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceInfoQueryRequest 查询请求
     * @return 通用返回类
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        }
        // QueryWrapper用于构建查询条件，可以通过链式调用的方式组装各种查询条件
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        List<UserInterfaceInfo> interfaceList = userInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceList);
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest 查询请求
     * @return 通用返回类
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfoAndNameResponse>> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest)
    {
        UserInterfaceInfoAndNameResponse userInterfaceInfoQuery = new UserInterfaceInfoAndNameResponse();
        long current = 0;
        long size = 0;
        Long userId = userInterfaceInfoQuery.getUserId();
        Long interfaceInfoId = userInterfaceInfoQuery.getInterfaceInfoId();
        Integer status = userInterfaceInfoQuery.getStatus();
        if (userInterfaceInfoQueryRequest != null) {
            // 当前页号,页面大小,排序字段,排序顺序（默认升序）
            current = userInterfaceInfoQueryRequest.getCurrent();
            size = userInterfaceInfoQueryRequest.getPageSize();
        }

        Page<UserInterfaceInfoAndNameResponse> userInterfaceInfoAndNameResponseList =
                userInterfaceInfoMapper.getUserInterfaceInfoResponseList(new Page<>(current, size));

        return ResultUtils.success(userInterfaceInfoAndNameResponseList);
    }

    /**
     * 分页获取用户自己的所有接口调用情况
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page/user")
    public BaseResponse<Page<UserInterfaceInfoAndNameResponse>> getUserInterfaceInfoByUserByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request)
    {
        UserInterfaceInfoAndNameResponse userInterfaceInfoQuery = new UserInterfaceInfoAndNameResponse();
        long current = 0;
        long size = 0;
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
            // 当前页号,页面大小,排序字段,排序顺序（默认升序）
            current = userInterfaceInfoQueryRequest.getCurrent();
            size = userInterfaceInfoQueryRequest.getPageSize();
        }
        User user = userService.getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Page<UserInterfaceInfoAndNameResponse> userInterfaceInfoAndNameResponseList =
                userInterfaceInfoMapper.getUserInterfaceInfoAndNameResponseList(new Page<>(current, size), user.getId());

        return ResultUtils.success(userInterfaceInfoAndNameResponseList);
    }

    /**
     * 开通获取调用次数
     */
    @PutMapping("/addNum")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addUserInterfaceInfoLeftNum(@RequestBody UserInterfaceInfoAddNumRequest userInterfaceInfoAddNumRequest,
                                                             HttpServletRequest request)
    {
        Long id = userInterfaceInfoAddNumRequest.getId();
        int num = userInterfaceInfoAddNumRequest.getNum();

        if (num <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean b = userInterfaceInfoService.addLeftNum(id, num);

        return ResultUtils.success(b);
    }
    // endregion

}
