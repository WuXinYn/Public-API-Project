package com.wxy.api.common.model.dto.interfaceinfo;

import com.wxy.api.common.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
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
public class InterfaceInfoQueryRequest extends PageRequest {

    /**
     * 主键
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建人
     */
    private Long userID;

}