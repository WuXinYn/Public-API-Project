package com.wxy.api.common.model.dto.userinterfaceinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增次数请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserInterfaceInfoAddNumRequest implements Serializable
{
    /**
     * 主键
     */
    @NonNull
    private Long id;

    @NonNull
    private Integer num;

    @Serial
    private static final long serialVersionUID = 1L;
}
