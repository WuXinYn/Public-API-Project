package com.wxy.api.common.model.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RouteRequest {
    @NotEmpty
    private String url;
    @NotEmpty
    private String method;
}
