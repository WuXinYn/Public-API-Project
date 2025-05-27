package com.wxy.api.common.common;

import lombok.Data;
import javax.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;

/**
 * 删除请求
 *
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    @Min(0)
    private long id;

    @Serial
    private static final long serialVersionUID = 1L;
}