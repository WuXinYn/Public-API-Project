package com.wxy.api.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户购买套餐记录
 * &#064;TableName  user_order_info
 */
@TableName(value ="user_order_info")
@Data
public class UserOrderInfo
{
    /**
     * 记录编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField(value = "order_number")
    private String orderNumber;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 接口id
     */
    @TableField(value = "interface_id")
    private Long interfaceId;

    /**
     * 加油包id
     */
    @TableField(value = "set_menu_id")
    private Long setMenuId;

    /**
     * 套餐名称
     */
    @TableField(value = "set_menu_name")
    private String setMenuName;

    /**
     * 加油包含量
     */
    @TableField(value = "set_menu_number")
    private Integer setMenuNumber;

    /**
     * 数量
     */
    @TableField(value = "number")
    private Integer number;

    /**
     * 支付金额
     */
    @TableField(value = "payment_number")
    private BigDecimal paymentNumber;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 支付方式(0：微信；1：支付宝)
     */
    @TableField(value = "payment_method")
    private Integer paymentMethod;

    /**
     * 订单状态(0：待支付、1：已支付、2：已取消、3：支付失败)
     */
    @TableField(value = "order_status")
    private Integer orderStatus;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    private Date payTime;

    /**
     * 是否生效(0：未生效；1：生效)
     */
    @TableField(value = "is_normal")
    private Integer isNormal;

    /**
     * 逻辑删除（0：未删；1：已删）
     */
    @TableLogic
    private Integer isDelete;
}