package com.wxy.api.common.model.dto.fuelpackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FuelPackageInfoVo implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 加油包ID
     */
    private long id;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 加油包含量
     */
    private int amount;

}
