package com.wxy.api.common.model.dto.fuelpackage;

import com.wxy.api.common.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
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
public class FuelPackageQueryRequest extends PageRequest{

    /**
     * 加油包ID
     */
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
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
