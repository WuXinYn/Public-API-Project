package com.wxy.api.backend.service;

import com.wxy.api.common.model.entity.UserOrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
* @author Administrator
* @description 针对表【user_order_info(用户购买套餐记录)】的数据库操作Service
* @createDate 2025-03-07 10:45:41
*/
public interface UserOrderInfoService extends IService<UserOrderInfo> {

    /**
     * 根据订单编号查询订单详情
     */
    UserOrderInfo getUserOrderInfo(String orderNumber);

    /**
     * 购买加油包成功后更新订单
     *
     * @param order 订单信息
     */
    void updatePayOrderInfo(UserOrderInfo order, Date payTime);

    /**
     * 退款成功后更新订单
     *
     * @param order 订单信息
     */
    void updateRefundOrderInfo(UserOrderInfo order);
}
