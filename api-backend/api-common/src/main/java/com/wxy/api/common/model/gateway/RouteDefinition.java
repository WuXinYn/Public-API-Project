package com.wxy.api.common.model.gateway;

import lombok.Data;

import java.util.List;

@Data
public class RouteDefinition {
    private String id;
    private String uri;
    private List<PredicateDefinition> predicates;
    private List<FilterDefinition> filters;
}
