package com.wxy.api.common.model.dto.userorderinfo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FuelPackageOrderGenerateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NonNull
    @Min(0)
    private Long userId;

    @NonNull
    @Min(0)
    private Long interfaceId;

    @NonNull
    @Min(0)
    private Long fuelPackageId;

    @Builder.Default
    private Integer payMethod = 1;
}
