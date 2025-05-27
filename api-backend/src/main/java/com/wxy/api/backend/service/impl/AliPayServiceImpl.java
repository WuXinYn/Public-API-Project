package com.wxy.api.backend.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.easysdk.factory.Factory;
import com.google.gson.Gson;
import com.wxy.api.backend.config.AliPayConfig;
import com.wxy.api.backend.service.*;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.dto.userorderinfo.ZfbPayCallBackMsg;
import com.wxy.api.common.model.entity.UserOrderInfo;
import com.wxy.api.common.model.enums.PayEnums;
import com.wxy.api.common.model.vo.AliPayRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.wxy.api.common.model.enums.PayEnums.FAST_INSTANT_TRADE_PAY;
import static com.wxy.api.common.model.enums.PayEnums.FAST_INSTANT_TRADE_REFUND;
import static com.wxy.api.common.model.enums.ZfbPayEnums.ORDER_PAYMENT_STATUS_SUCCESS;

@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService
{

    @Resource
    private AliPayConfig aliPayConfig;

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "utf-8";
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserOrderInfoService userOrderInfoService;

    @Resource
    private PayService payService;

    @Resource
    private RabbitTemplate rabbitTemplate; // todo 用于发送消息到MQ，异步优化，待优化

    /**
     * 支付宝支付
     *
     * @param aliPay 支付宝对象
     * @param httpResponse httpResponse
     */
    @Override
    public String payByPost(AliPayRequest aliPay, HttpServletResponse httpResponse)
    {
        log.info("=========支付宝POST支付========");

        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                                                            aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(),
                                                            SIGN_TYPE);
        AlipayTradePagePayRequest request = getAlipayTradePagePayRequest(aliPay);
        String form;
        try {
            // 调用SDK生成表单
            form = alipayClient.pageExecute(request).getBody();
        }
        catch (AlipayApiException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "支付宝支付异常！"+e.getMessage());
        }
        // 直接将完整的表单html输出到页面
        return form;
    }

    /**
     * 退款
     *
     * @param aliPay 支付宝对象
     */
    @Override
    public String toRefund(AliPayRequest aliPay, HttpServletResponse response){
        System.out.println("=========支付宝退款=========");
        // 1. 设置编码格式
        response.setContentType("text/html;charset=utf-8");
        // 2. 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(),
                SIGN_TYPE);
        // 3. 设置请求参数
        AlipayTradeRefundRequest request = getAlipayTradeRefundRequest(aliPay);
        AlipayTradeRefundResponse resp;
        try {
            //请求
            resp = alipayClient.execute(request);
        }
        catch (AlipayApiException e) {
            log.error("支付宝退款调用失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付宝退款调用服务异常，请联系管理员处理 : "+e.getMessage());
        }
        if (resp != null && resp.isSuccess()) {
            UserOrderInfo order = userOrderInfoService.getUserOrderInfo(aliPay.getOrderNumber());
            // 3. 如果是退款成功的回调消息
            new Thread(() -> {
                userOrderInfoService.updateRefundOrderInfo(order); // 更新订单状态
                userInterfaceInfoService.updateRefundUserInterfaceInfo(order); // 更新用户资源信息
            }).start();

            // 输出
            return resp.getBody();
        }
        else {
            log.error("支付宝退款调用失败");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付宝退款调用失败，请联系管理员处理");
        }
    }

    /**
     * 支付宝异步回调
     *
     * @param request 请求
     * @return 成功：success
     */
    @Override
    public String payNotify(HttpServletRequest request){
        log.info("=========支付宝异步回调========");
        String tradeStatus = request.getParameter("trade_status");
        if (tradeStatus == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"支付宝回调异常：tradeStatus is null");
        }
        if (tradeStatus.equals(ORDER_PAYMENT_STATUS_SUCCESS.getInfo())) {

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            // 支付宝验签
            try {
                if (Boolean.TRUE.equals(Factory.Payment.Common().verifyNotify(params))) {
                    // 验签通过
                    log.info("===支付宝异步回调-验签通过===");
                    ZfbPayCallBackMsg zfbPayCallBackMsg = ZfbPayCallBackMsg.builder()
                            .serviceName(params.get("subject"))
                            .tradeNo(params.get("trade_no"))
                            .orderNumber(params.get("out_trade_no"))
                            .totalAmount(params.get("total_amount"))
                            .buyerId(params.get("buyer_id"))
                            .buyerPayAmount(params.get("buyer_pay_amount"))
                            .gmtPayment(params.get("gmt_payment"))
                            .productCode(params.get("product_code"))
                            .build();
                    
                    // 记录完整回调参数，便于调试
                    log.info("支付宝回调参数: {}", params);

                    handleCallback(zfbPayCallBackMsg);
                }
                else {
                    // 验签失败
                    return PayEnums.FAILURE.getMessage();
                }
            }
            catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付宝回调异常(支付宝验签error)："+e);
            }
        }
        else {
            log.error("支付宝回调异常：tradeStatus is not success---details: {}", tradeStatus);
        }
        return PayEnums.SUCCESS.getMessage();
    }

    /**
     * 异步更新订单和用户资源信息 TODO队列优化
     *
     * @param zfbPayCallBackMsg 支付宝回调信息
     */
    private void handleCallback(ZfbPayCallBackMsg zfbPayCallBackMsg){
        // 1. 校验订单参数与支付宝回调参数是否匹配
        UserOrderInfo order = payService.validOrderInfo(zfbPayCallBackMsg);
        String productCode = zfbPayCallBackMsg.getProductCode();
        // 如果productCode为空，默认设为支付
        if (productCode == null) {
            log.warn("支付宝回调消息中productCode为空，默认设为支付操作");
            productCode = FAST_INSTANT_TRADE_PAY.getMessage();
        }
        String gmtPayment = zfbPayCallBackMsg.getGmtPayment();
        Date payTime = DateUtil.parse(gmtPayment);
        // 2. 选择业务
        if (productCode.equals(FAST_INSTANT_TRADE_PAY.getMessage())) {
            // 3. 如果是支付成功的回调消息
            new Thread(() -> {
                userOrderInfoService.updatePayOrderInfo(order, payTime); // 更新订单状态
                userInterfaceInfoService.updatePayUserInterfaceInfo(order); // 更新用户资源信息
            }).start();
        }
        else if (productCode.equals(FAST_INSTANT_TRADE_REFUND.getMessage())) {
            // 3. 如果是退款成功的回调消息
            new Thread(() -> {
                userOrderInfoService.updateRefundOrderInfo(order); // 更新订单状态
                userInterfaceInfoService.updateRefundUserInterfaceInfo(order); // 更新用户资源信息
            }).start();
        }
        else {
            // 3. 其他类型信息
            log.info("支付宝回调消息-异常消息-{}", productCode);
        }
    }

    /**
     * 获取支付宝支付请求
     *
     * @param aliPay 支付宝业务相关信息
     */
    private AlipayTradePagePayRequest getAlipayTradePagePayRequest(AliPayRequest aliPay)
    {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        // 保持必要的标准参数，自定义参数可以作为扩展参数
        request.setBizContent("{\"out_trade_no\":\"" + aliPay.getOrderNumber() + "\","
                                      + "\"total_amount\":\"" + aliPay.getTotalAmount() + "\","
                                      + "\"subject\":\"" + aliPay.getServiceName() + "\","
                                      + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        return request;
    }

    /**
     *  获取支付宝退款请求
     */
    private AlipayTradeRefundRequest getAlipayTradeRefundRequest(AliPayRequest aliPay)
    {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setReturnUrl(aliPayConfig.getReturnUrl());
        request.setBizContent("{\"out_trade_no\":\"" + aliPay.getOrderNumber() + "\","
                + "\"refund_amount\":\"" + aliPay.getTotalAmount() + "\","
                + "\"refund_reason\":\"" + "正常退款" + "\","
//                + "\"fuel_package_id\":\"" + aliPay.getFuelPackageId() + "\","
//                + "\"fuel_package_amount\":\"" + aliPay.getAmount() + "\","
//                + "\"product_number\":\"" + aliPay.getNumber() + "\","
                + "\"service_name\":\"" + aliPay.getServiceName() + "\","
                + "\"operator_id\":\"" + aliPay.getUserId() + "\","
                + "\"product_code\":\"" + FAST_INSTANT_TRADE_REFUND.getMessage() + "\"}");
        return request;
    }

}
