package com.wxy.api.common.model.dto.fuelpackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新请求
 *
 * &#064;TableName  product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FuelPackageUpdateRequest implements Serializable {

    /**
     * 加油包ID
     */
    @NotNull(message = "加油包ID不能为空")
    @Min(0)
    private Long id;

    @NotEmpty
    private String name;

    /**
     * 加油包描述
     */
    private String description;

    /**
     * 加油包含量
     */
    @NotNull(message = "加油包含量不能为空")
    @Min(0)
    private Integer amount;

    /**
     * 价格
     */
    @NotNull
    private BigDecimal price;


    @Serial
    private static final long serialVersionUID = 1L;
}
