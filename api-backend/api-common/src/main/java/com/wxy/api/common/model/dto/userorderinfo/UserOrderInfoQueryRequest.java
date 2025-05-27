package com.wxy.api.common.model.dto.userorderinfo;

import com.wxy.api.common.common.PageRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询请求
 *
 */
// @EqualsAndHashCode用于自动生成equals(Object other)和hashCode()方法
@EqualsAndHashCode(callSuper = true) //callSuper属性设置为true表示要调用父类的equals和hashCode方法，以确保在多层继承结构中也能正确比较对象的相等性。
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserOrderInfoQueryRequest extends PageRequest {

    /**
     * 记录编号
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNumber;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 套餐id
     */
    private Long setMenuId;

    /**
     * 套餐名称
     */
    private String setMenuName;

    /**
     * 支付金额
     */
    private BigDecimal paymentNumber;

    /**
     * 支付方式(0：微信；1：支付宝)
     */
    private Integer paymentMethod;

    /**
     * 订单状态(0：待支付、1：已支付、2：已取消、3：支付失败)
     */
    private Integer orderStatus;

    /**
     * 支付时间
     */
    private Date payTime;
}
