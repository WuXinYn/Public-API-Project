package com.wxy.api.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wxy.api.backend.service.*;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.dto.userorderinfo.ZfbPayCallBackMsg;
import com.wxy.api.common.model.entity.FuelPackage;
import com.wxy.api.common.model.entity.User;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import com.wxy.api.common.model.entity.UserOrderInfo;
import com.wxy.api.common.model.enums.InterfaceInfoStatusEnum;
import com.wxy.api.common.model.enums.OrderEnums;
import com.wxy.api.common.model.enums.PayEnums;
import com.wxy.api.common.utils.OrderNumberGenerator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

import static com.wxy.api.common.model.enums.PayEnums.FAST_INSTANT_TRADE_PAY;
import static com.wxy.api.common.model.enums.PayEnums.FAST_INSTANT_TRADE_REFUND;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Resource
    private UserService userService;

    @Resource
    private FuelPackageService fuelPackageService;

    @Resource
    private UserOrderInfoService userOrderInfoService;


    /**
     * 生成用户购买加油包订单
     *
     * @param userId        用户ID
     * @param fuelPackageId 加油包ID
     * @return 订单编号
     */
    @Override
    public String generateFuelPackageOrderInfo(@Min(0) long userId,long interfaceInfoId, @Min(0) long fuelPackageId, @Min(0) int payMethod) {
        // 1. 加油包信息
        FuelPackage fuelPackage = fuelPackageService.getById(fuelPackageId);
        if (fuelPackage == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成购买加油包订单-加油包不存在");
        }
        // 2. 创建订单
        UserOrderInfo order = new UserOrderInfo();
        order.setUserId(userId);
        order.setInterfaceId(interfaceInfoId);
        order.setSetMenuId(fuelPackageId);
        order.setSetMenuName(fuelPackage.getName());
        order.setSetMenuNumber(fuelPackage.getAmount());
        order.setPaymentMethod(payMethod);
        BigDecimal number = BigDecimal.valueOf(fuelPackage.getNumber());
        BigDecimal add = fuelPackage.getPrice().multiply(number);
        order.setPaymentNumber(add);
        order.setOrderNumber(OrderNumberGenerator.generateOrderNumber(userId));
        boolean save = userOrderInfoService.save(order);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成购买加油包订单-创建订单失败");
        }
        return order.getOrderNumber();
    }

    /**
     * 校验订单参数
     *
     * @param orderNumber 订单编号
     * @return 购买套餐订单信息
     */
    @Override
    public UserOrderInfo validateFuelPackageOrder(@NotEmpty String orderNumber, @NotEmpty String productCode) {
        UserOrderInfo order = userOrderInfoService.getUserOrderInfo(orderNumber);
        if (productCode.equals(FAST_INSTANT_TRADE_PAY.getMessage())) { // 支付
            if (!order.getOrderStatus().equals(OrderEnums.TO_BE_PAID.getCode())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "支付-订单状态异常-"+order.getOrderStatus());
            }
        }
        else if(productCode.equals(FAST_INSTANT_TRADE_REFUND.getMessage())){ // 退款
            if (!order.getOrderStatus().equals(OrderEnums.PAID_ED.getCode())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "退款-订单状态异常-"+order.getOrderStatus());
            }
        }
        if (order.getPayTime() != null || !Objects.equals(order.getIsNormal(), PayEnums.NOT_NORMAL.getCode()) || order.getOrderStatus().equals(OrderEnums.PAID_ED.getCode())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单已支付,请勿重复支付");
        }
        return order;
    }

    /**
     * 校验订单参数与支付宝回调参数是否匹配
     *
     * @param zfbPayCallBackMsg 支付宝回调信息
     */
    @Override
    public UserOrderInfo validOrderInfo(@NotNull ZfbPayCallBackMsg zfbPayCallBackMsg){
        String orderNumber = zfbPayCallBackMsg.getOrderNumber();
        String productCode = zfbPayCallBackMsg.getProductCode();
        
        // 仅验证订单号和支付状态，支付宝回调可能没有其他字段
        if (productCode == null) {
            log.warn("支付宝回调productCode为空，默认设置为支付操作");
            productCode = FAST_INSTANT_TRADE_PAY.getMessage();
        }
        
        UserOrderInfo order = this.validateFuelPackageOrder(orderNumber, productCode);
        
        // 校验支付金额
        if (zfbPayCallBackMsg.getTotalAmount() != null) {
            BigDecimal totalAmount = new BigDecimal(zfbPayCallBackMsg.getTotalAmount());
            if (totalAmount.compareTo(order.getPaymentNumber()) != 0) {
                String msg = "支付宝-订单编号: "+zfbPayCallBackMsg.getOrderNumber()+"-回调参数异常-";
                throw new BusinessException(ErrorCode.OPERATION_ERROR, msg+"支付金额与订单不匹配");
            }
        }
        
        return order;
    }
}
