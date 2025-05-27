package com.wxy.api.common.model.dto.userorderinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ZfbPayCallBackMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 交易名称
     */
    private String serviceName;

    /**
     * 支付宝交易凭证号
     */
    @NotEmpty
    private String tradeNo;

    /**
     * 商户订单号
     */
    @NotEmpty
    @Size(min = 13, message = "订单编号不合规")
    private String orderNumber;

    /**
     * 交易金额
     */
    @NotEmpty
    private String totalAmount;

    /**
     * 买家在支付宝唯一id
     */
    @NotEmpty
    private String buyerId;

    /**
     * 用户id
     */
//    @NotEmpty
//    private String userId;

    /**
     * 买家付款时间
     */
    @NotEmpty
    private String gmtPayment;

    /**
     * 买家付款金额
     */
    @NotEmpty
    private String buyerPayAmount;

    /**
     * 加油包id
     */
//    @NotEmpty
//    private String fuelPackageId;

    /**
     * 加油包含量
     */
//    @NotEmpty
//    private String amount;

    /**
     * 商品数量
     */
//    private String number;

    /**
     * 支付 OR 退款
     */
    @NotEmpty
    private String productCode;
}
