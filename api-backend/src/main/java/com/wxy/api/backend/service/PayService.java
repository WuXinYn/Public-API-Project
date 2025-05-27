package com.wxy.api.backend.service;

import com.wxy.api.common.model.dto.userorderinfo.ZfbPayCallBackMsg;
import com.wxy.api.common.model.entity.UserOrderInfo;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 支付服务
 */
public interface PayService {

    /**
     * 生成用户购买加油包订单
     *
     * @param userId        用户ID
     * @param fuelPackageId 加油包ID
     * @return 订单编号
     */
    String generateFuelPackageOrderInfo(@Min(0) long userId, @Min(0) long interfaceInfoId, @Min(0) long fuelPackageId, @Min(0) int payMethod);

    /**
     * 校验订单参数
     *
     * @param orderNumber 订单编号
     * @return 用户购买套餐记录
     */
    UserOrderInfo validateFuelPackageOrder(@NotEmpty String orderNumber, @NotEmpty String productCode);

    /**
     * 校验订单参数与支付宝回调参数是否匹配
     * @param zfbPayCallBackMsg 支付宝回调信息
     */
    UserOrderInfo validOrderInfo(@NotNull ZfbPayCallBackMsg zfbPayCallBackMsg);
}
