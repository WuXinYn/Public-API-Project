package com.wxy.api.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.api.backend.annotation.AuthCheck;
import com.wxy.api.backend.service.NotificationInfoService;
import com.wxy.api.backend.service.UserService;
import com.wxy.api.common.common.*;
import com.wxy.api.common.constant.UserConstant;
import com.wxy.api.common.model.dto.notificationInfo.NotificationInfoAddRequest;
import com.wxy.api.common.model.dto.notificationInfo.NotificationInfoQueryRequest;
import com.wxy.api.common.model.dto.notificationInfo.NotificationInfoUpdateStatusRequest;
import com.wxy.api.common.model.entity.NotificationInfo;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 通知管理
 *
 * @author wxy
 */
@RestController
@RequestMapping("/notificationInfo")
@Slf4j
public class NotificationInfoController
{
    @Resource
    private NotificationInfoService notificationInfoService;

    @Resource
    private InnerUserService innerUserService;

    @Resource
    private UserService userService;

    /**
     * 发送通知
     *
     * @param notificationInfoAddRequest 通知信息
     * @return BaseResponse<Boolean>
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> addNewNotification(@RequestBody NotificationInfoAddRequest notificationInfoAddRequest){
        // 校验参数
        if (notificationInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        Long userId = notificationInfoAddRequest.getUser_id();
        String title = notificationInfoAddRequest.getTitle();
        String content = notificationInfoAddRequest.getContent();
        String type = notificationInfoAddRequest.getType();
        if (StringUtils.isAnyBlank(title, content, type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        if (userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接收该通知的用户id数据异常");
        }

        // 给所有用户发通知
        boolean result = false;
        if (notificationInfoAddRequest.getUser_id() == 0) {
            List<User> allUsers = innerUserService.getAllUsers();
            for (User item : allUsers) {
                NotificationInfo i = new NotificationInfo();
                BeanUtils.copyProperties(notificationInfoAddRequest, i);
                i.setUser_id(item.getId());
                result = notificationInfoService.save(i);
                if (!result) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "给所有用户发送通知事务失败");
                }
            }
        }
        else {
            // 给单个用户发通知
            NotificationInfo i = new NotificationInfo();
            BeanUtils.copyProperties(notificationInfoAddRequest, i);
            result = notificationInfoService.save(i);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "给单个用户发送通知事务失败");
            }
        }
        return ResultUtils.success(result);
    }

    /**
     * 删除通知（普通用户按id删除自己的通知）
     *
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteNotification(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        // 参数验证
        if (deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        long id = deleteRequest.getId();
        NotificationInfo notificationInfo = notificationInfoService.getById(id);
        if (notificationInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "通知消息不存在或已失效");
        }
        // 仅本人或管理员可删除
        User user = userService.getLoginUser(request);
        if(!notificationInfo.getUser_id().equals(user.getId()) && !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "您没有删除改通知的权限");
        }
        boolean result = notificationInfoService.removeById(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除通知事务失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 普通用户获取自己的通知列表
     *
     * @param notificationInfoQueryRequest 查询条件
     * @return 通用返回类
     */
    @GetMapping("/list")
    public BaseResponse<List<NotificationInfo>> listInterfaceInfoByUser(NotificationInfoQueryRequest notificationInfoQueryRequest, HttpServletRequest request) {
        // 权限校验
        User user = userService.getLoginUser(request);
        NotificationInfo notificationInfo = new NotificationInfo();
        if (notificationInfoQueryRequest != null) {
            BeanUtils.copyProperties(notificationInfoQueryRequest, notificationInfo);
        }
        if(!notificationInfo.getUser_id().equals(user.getId()) && !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<NotificationInfo> queryWrapper = new QueryWrapper<>(notificationInfo);
        queryWrapper.orderByDesc("created_at"); // 降序
        List<NotificationInfo> notificationInfoList = notificationInfoService.list(queryWrapper);
        return ResultUtils.success(notificationInfoList);
    }

    /**
     * 获取列表（管理员获取所有通知的列表）
     *
     */
    @GetMapping("/list/all/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<NotificationInfo>> listAllNotificationInfos(NotificationInfoQueryRequest notificationInfoQueryRequest, HttpServletRequest request) {
        // 当前页号,页面大小,排序字段,排序顺序（默认升序）
        long current = notificationInfoQueryRequest.getCurrent();
        long size = notificationInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // mybatis-plus的QueryWrapper
        QueryWrapper<NotificationInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at"); // 降序
        Page<NotificationInfo> notificationInfoPage = notificationInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(notificationInfoPage);
    }

    /**
     * 分页获取列表(普通用户获取自己的)
     *
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<NotificationInfo>> listNotificationInfoByPage(NotificationInfoQueryRequest notificationInfoQueryRequest, HttpServletRequest request) {
        // 权限校验
        User user = userService.getLoginUser(request);
        long current = 0;
        long size = 0;
        NotificationInfo notificationInfoQuery = new NotificationInfo();
        if (notificationInfoQueryRequest != null) {
            BeanUtils.copyProperties(notificationInfoQueryRequest, notificationInfoQuery);
            // 当前页号,页面大小,排序字段,排序顺序（降序）
            current = notificationInfoQueryRequest.getCurrent();
            size = notificationInfoQueryRequest.getPageSize();
        }
        Long userId = notificationInfoQuery.getUser_id();
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        else if(!userId.equals(user.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
//        String sortField = notificationInfoQueryRequest.getSortField();
//        String sortOrder = notificationInfoQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        // mybatis-plus的QueryWrapper
        QueryWrapper<NotificationInfo> queryWrapper = new QueryWrapper<>(notificationInfoQuery);
        // isNotBlank判断某字符串是否非空
//        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
//                             sortOrder.equals(CommonConstant.SORT_ORDER_DESC), sortField);
        queryWrapper.orderByDesc("created_at");
        Page<NotificationInfo> notificationInfoPage = notificationInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(notificationInfoPage);
    }

    /**
     * 更新通知状态为已读
     */
    @PostMapping("/update/status")
    public BaseResponse<Boolean> updateNotificationStatus(@RequestBody NotificationInfoUpdateStatusRequest notificationInfoUpdateStatusRequest, HttpServletRequest request) {
        if (notificationInfoUpdateStatusRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<NotificationInfo> updateWrapper =  new UpdateWrapper<>();
        updateWrapper.eq("id", notificationInfoUpdateStatusRequest.getId())
                     .set("status", "read");
        boolean update = notificationInfoService.update(updateWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新通知状态事务失败");
        }
        return ResultUtils.success(true);
    }
}
