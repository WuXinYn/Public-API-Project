package com.wxy.api.common.model.vo;

import com.wxy.api.common.model.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息封装器
 *
 * &#064;TableName  product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends InterfaceInfo
{

    /**
     * 调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}