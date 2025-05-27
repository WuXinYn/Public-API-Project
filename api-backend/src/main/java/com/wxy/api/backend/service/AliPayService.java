package com.wxy.api.backend.service;

import com.wxy.api.common.model.vo.AliPayRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AliPayService
{

    /**
     * 支付宝支付
     *
     * @param aliPay 支付宝对象
     * @param httpResponse httpResponse
     */
    String payByPost(AliPayRequest aliPay, HttpServletResponse httpResponse);

    /**
     * 支付宝异步回调
     *
     * @param request 请求
     * @return 成功：success
     */
    String payNotify(HttpServletRequest request);

    /**
     * 退款
     *
     * @param aliPay 支付宝对象
     * @param response
     * @return
     */
    String toRefund(AliPayRequest aliPay, HttpServletResponse response);
}
