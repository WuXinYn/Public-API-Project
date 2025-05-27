package com.wxy.api.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.api.backend.annotation.AuthCheck;
import com.wxy.api.backend.service.UserOrderInfoService;
import com.wxy.api.common.common.BaseResponse;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.common.ResultUtils;
import com.wxy.api.common.constant.CommonConstant;
import com.wxy.api.common.model.dto.userorderinfo.UserOrderInfoQueryRequest;
import com.wxy.api.common.model.entity.UserOrderInfo;
import com.wxy.api.common.model.enums.OrderEnums;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 购买订单记录接口
 */
@RestController
@RequestMapping("/order")
public class UserOrderController {
    @Resource
    private UserOrderInfoService userOrderInfoService;

    /**
     * 分页+条件查询订单记录列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserOrderInfo>> getUserMenuInfoByUserID(UserOrderInfoQueryRequest userOrderInfoQueryRequest, HttpServletRequest request) {
        UserOrderInfo userOrderInfo = new UserOrderInfo();
        long current = 0;
        long size = 0;
        String sortField = "create_time";
        String sortOrder = "descend";
        if (userOrderInfoQueryRequest != null) {
            BeanUtils.copyProperties(userOrderInfoQueryRequest, userOrderInfo);
            // 当前页号,页面大小,排序字段,排序顺序（默认升序）
            current = userOrderInfoQueryRequest.getCurrent();
            size = userOrderInfoQueryRequest.getPageSize();
//            sortField = userOrderInfoQueryRequest.getSortField();
//            sortOrder = userOrderInfoQueryRequest.getSortOrder();
        }

        // setMenuName 需支持模糊搜索
        String setMenuName = userOrderInfo.getSetMenuName();
        userOrderInfo.setSetMenuName(null);

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<UserOrderInfo> queryWrapper = new QueryWrapper<>(userOrderInfo);
        // isNotBlank判断某字符串是否非空
        queryWrapper.like(StringUtils.isNotBlank(setMenuName), "set_menu_name", setMenuName);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserOrderInfo> userOrderInfoPage = userOrderInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userOrderInfoPage);
    }

    /**
     * 获取订单记录详情
     */
    @GetMapping("/get_by_id")
    public BaseResponse<UserOrderInfo> getUserOrderInfoById(@RequestParam Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        UserOrderInfo byId = userOrderInfoService.getById(id);
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "记录不存在");
        }
        return ResultUtils.success(byId);
    }

    /**
     * 取消订单
     */
    @GetMapping("/cancel")
    public BaseResponse<Boolean> cancelOrder(@RequestParam Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        UserOrderInfo byId = userOrderInfoService.getById(id);
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        if (!Objects.equals(byId.getOrderStatus(), OrderEnums.TO_BE_PAID.getCode())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能取消未付款订单");
        }
        byId.setOrderStatus(OrderEnums.CANCELED.getCode());
        boolean cancel = userOrderInfoService.updateById(byId);
        return ResultUtils.success(cancel);
    }

    /**
     * 修改订单信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateFuelPackage(@RequestBody UserOrderInfo userOrderInfo) {
        if (userOrderInfo == null || userOrderInfo.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean update = userOrderInfoService.updateById(userOrderInfo);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新事务失败");
        }
        return ResultUtils.success(update);
    }

    /**
     * 删除订单信息
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteFuelPackage(@RequestParam("id") Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        boolean remove = userOrderInfoService.removeById(id);
        return ResultUtils.success(remove);
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public BaseResponse<Boolean> deleteBatch(@RequestParam("ids") List<Long> ids) {
        if (ids == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id不能为空");
        }
        boolean remove = userOrderInfoService.removeByIds(ids);
        return ResultUtils.success(remove);
    }

    /**
     * 分页+条件查询所有订单记录列表 管理员
     */
    @GetMapping("/list/all")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserOrderInfo>> getAllOrderInfos(UserOrderInfoQueryRequest userOrderInfoQueryRequest, HttpServletRequest request) {
        UserOrderInfo userOrderInfo = new UserOrderInfo();
        long current = 0;
        long size = 0;
        String sortField = "create_time";
        String sortOrder = "descend";
        if (userOrderInfoQueryRequest != null) {
            BeanUtils.copyProperties(userOrderInfoQueryRequest, userOrderInfo);
            // 当前页号,页面大小,排序字段,排序顺序（默认升序）
            current = userOrderInfoQueryRequest.getCurrent();
            size = userOrderInfoQueryRequest.getPageSize();
            sortField = userOrderInfoQueryRequest.getSortField();
            sortOrder = userOrderInfoQueryRequest.getSortOrder();
            userOrderInfo.setUserId(null);
        }

        // setMenuName 需支持模糊搜索
        String setMenuName = userOrderInfo.getSetMenuName();
        userOrderInfo.setSetMenuName(null);

        QueryWrapper<UserOrderInfo> queryWrapper = new QueryWrapper<>(userOrderInfo);
        // isNotBlank判断某字符串是否非空
        queryWrapper.like(StringUtils.isNotBlank(setMenuName), "set_menu_name", setMenuName);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                             sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserOrderInfo> userOrderInfoPage = userOrderInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userOrderInfoPage);
    }
}
