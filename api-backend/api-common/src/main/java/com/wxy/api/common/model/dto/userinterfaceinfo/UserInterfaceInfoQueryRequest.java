package com.wxy.api.common.model.dto.userinterfaceinfo;

import com.wxy.api.common.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

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
public class UserInterfaceInfoQueryRequest extends PageRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 请求用户id
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

}