package com.wxy.api.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 加油包表
 * @TableName fuel_package
 */
@TableName(value ="fuel_package")
@Data
public class FuelPackage {
    /**
     * 加油包ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 加油包名称
     */
    private String name;

    /**
     * 加油包描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 加油包含量
     */
    private Integer amount;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 逻辑删除（0：未删；1：已删）
     */
    @TableLogic
    private Integer isDelete;
}