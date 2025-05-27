package com.wxy.api.common.model.vo;

import lombok.Data;

/**
 * 判断接口存活信息
 * &#064;TableName  interface_info
 */
@Data
public class InterfaceAliveInfo
{
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 是否删除(0-未删, 1-已删)
     * 逻辑删除
     */
    private Integer isDelete;
}
