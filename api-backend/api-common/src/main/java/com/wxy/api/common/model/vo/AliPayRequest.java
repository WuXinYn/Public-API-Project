package com.wxy.api.common.model.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 支付宝业务相关信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AliPayRequest implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    @Min(0)
    private long userId; //用户ID

    @NotEmpty
    @Size(min = 13, message = "订单编号不合规")
    private String orderNumber; //订单编号

    @NonNull
    @DecimalMin("0.00")
    private BigDecimal totalAmount;//订单总金额

    @Min(0)
    private long fuelPackageId; //商品ID

    private int amount; //加油包含量

    @Builder.Default
    private int number = 1; //商品数量

    @Builder.Default
    private String serviceName = "加油包"; // 商品服务名称
}
