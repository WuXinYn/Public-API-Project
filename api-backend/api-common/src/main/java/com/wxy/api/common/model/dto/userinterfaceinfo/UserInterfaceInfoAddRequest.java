package com.wxy.api.common.model.dto.userinterfaceinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

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
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * 请求用户id
     */
    @NonNull
    private Long userId;

    /**
     * 接口id
     */
    @NonNull
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

    @Serial
    private static final long serialVersionUID = 1L;

}