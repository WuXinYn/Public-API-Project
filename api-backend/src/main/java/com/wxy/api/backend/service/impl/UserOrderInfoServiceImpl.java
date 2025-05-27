package com.wxy.api.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.dto.userorderinfo.ZfbPayCallBackMsg;
import com.wxy.api.common.model.entity.UserOrderInfo;
import com.wxy.api.backend.service.UserOrderInfoService;
import com.wxy.api.backend.mapper.UserOrderInfoMapper;
import com.wxy.api.common.model.enums.OrderEnums;
import com.wxy.api.common.model.enums.PayEnums;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author Administrator
* &#064;description  针对表【user_order_info(用户购买套餐记录)】的数据库操作Service实现
* &#064;createDate  2025-03-07 10:45:41
 */
@Service
public class UserOrderInfoServiceImpl extends ServiceImpl<UserOrderInfoMapper, UserOrderInfo>
    implements UserOrderInfoService{

    /**
     * 根据订单编号查询订单详情
     */
    @Override
    public UserOrderInfo getUserOrderInfo(String orderNumber){
        if (StringUtils.isBlank(orderNumber)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询订单-订单编号不能为空");
        }
        QueryWrapper<UserOrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_number", orderNumber);
        UserOrderInfo one = this.getOne(queryWrapper);
        if (one == null || one.getId() <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询订单-订单不存在");
        }
        return one;
    }

    /**
     * 购买加油包成功后更新订单
     *
     * @param order 订单信息
     */
    @Override
    public void updatePayOrderInfo(UserOrderInfo order, Date payTime){
        UpdateWrapper<UserOrderInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_number", order.getOrderNumber());
        updateWrapper.set("pay_time", payTime);
        updateWrapper.set("order_status", OrderEnums.PAID_ED.getCode());
        updateWrapper.set("is_normal", PayEnums.NORMAL.getCode());
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新订单事务失败");
        }
    }

    /**
     * 退款成功后更新订单
     *
     * @param order 订单信息
     */
    @Override
    public void updateRefundOrderInfo(UserOrderInfo order){
        UpdateWrapper<UserOrderInfo> updateWrapper = new UpdateWrapper<>(order);
        updateWrapper.set("is_normal", PayEnums.NOT_NORMAL.getCode());
        updateWrapper.set("order_status", OrderEnums.REFUNDED.getCode());
        boolean update = this.update(updateWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新订单事务失败");
        }
    }

}




