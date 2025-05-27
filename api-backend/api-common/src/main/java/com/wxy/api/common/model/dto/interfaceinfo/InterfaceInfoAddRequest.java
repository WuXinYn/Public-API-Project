package com.wxy.api.common.model.dto.interfaceinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;

/**
 * 创建请求
 *
 * &#064;TableName  product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    @NotEmpty
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    @NotEmpty
    private String url;

    /**
     * 请求参数
     */
    @NotEmpty
    private String requestParams;

    /**
     * 请求头
     */
    @NotEmpty
    private String requestHeader;

    /**
     * 响应头
     */
    @NotEmpty
    private String responseHeader;

    /**
     * 请求类型
     */
    @NotEmpty
    private String method;

    @Serial
    private static final long serialVersionUID = 1L;
}