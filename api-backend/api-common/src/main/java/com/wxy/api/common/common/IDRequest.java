package com.wxy.api.common.common;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * id请求
 *
 * @author wxy
 */
@Data
public class IDRequest implements Serializable {

    /**
     * id
     */
    @Min(0)
    private long id;

    @Serial
    private static final long serialVersionUID = 1L;
}