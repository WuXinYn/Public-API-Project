package com.wxy.api.common.model.dto.interfaceinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 接口调用请求
 *
 * &#064;TableName  product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

    @Builder.Default
    private Boolean isTest = false;

    @Serial
    private static final long serialVersionUID = 1L;

}