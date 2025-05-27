package com.wxy.api.backend.controller;

import com.wxy.api.backend.service.AliPayService;
import com.wxy.api.backend.service.UserOrderInfoService;
import com.wxy.api.common.common.BaseResponse;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.common.ResultUtils;
import com.wxy.api.common.model.entity.UserOrderInfo;
import com.wxy.api.common.model.vo.AliPayRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 支付宝沙箱测试
 */
@RestController
@RequestMapping("/alipay")
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class AliPayController {

    @Resource
    private AliPayService aliPayService;

    @Resource
    private UserOrderInfoService userOrderInfoService;

    /**
     * POST支付
     * @param aliPay 支付宝参数
     * @param httpResponse httpResponse
     */
    @PostMapping("/pay/post")
    public BaseResponse<String> payByPost(@Validated @RequestBody AliPayRequest aliPay, HttpServletResponse httpResponse) {
        validParams(aliPay);
        String s = aliPayService.payByPost(aliPay, httpResponse);
        return ResultUtils.success(s);
    }

    /**
     * 支付宝异步回调
     * @param request 请求
     * @return 成功：SUCCESS
     */
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public BaseResponse<String> payNotify(HttpServletRequest request) {
        String s = aliPayService.payNotify(request);
        return ResultUtils.success(s);
    }

    /**
     * 退款
     * @param aliPay
     * @param response
     * @return
     */
    @PostMapping("/refund")
    public BaseResponse<String> toRefund(@Validated @RequestBody AliPayRequest aliPay, HttpServletResponse response)
    {
        validParams(aliPay);
        String s = aliPayService.toRefund(aliPay, response);
        return ResultUtils.success(s);
    }

    /**
     * 校验参数
     * @param aliPay 支付宝参数
     */
    public void validParams(@Validated AliPayRequest aliPay) {
        String orderNumber = aliPay.getOrderNumber();
        UserOrderInfo userOrderInfo = userOrderInfoService.getUserOrderInfo(orderNumber);
        BigDecimal amount = getBigDecimal(aliPay, userOrderInfo);
        if (amount.compareTo(userOrderInfo.getPaymentNumber()) != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付宝支付金额参数异常");
        }
    }

    @NotNull
    private static BigDecimal getBigDecimal(AliPayRequest aliPay, UserOrderInfo userOrderInfo) {
        String totalAmount = String.valueOf(aliPay.getTotalAmount());
        if (aliPay.getUserId() != userOrderInfo.getUserId()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付宝支付参数异常-用户id不匹配");
        }
        if (aliPay.getFuelPackageId() != userOrderInfo.getSetMenuId()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "支付宝支付参数异常-加油包id不匹配");
        }
        BigDecimal amount = new BigDecimal(totalAmount);
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        return amount;
    }
}
