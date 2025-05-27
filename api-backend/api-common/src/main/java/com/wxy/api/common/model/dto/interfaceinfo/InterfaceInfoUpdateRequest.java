package com.wxy.api.common.model.dto.interfaceinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * 更新请求
 *
 * &#064;TableName  product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    @Min(0)
    private long id;

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
     * 接口状态（0-关闭，1-开启）
     */
    @NotNull(message = "接口状态不能为空")
    private int status;

    /**
     * 请求类型
     */
    @NotEmpty
    private String method;

    @Serial
    private static final long serialVersionUID = 1L;

}