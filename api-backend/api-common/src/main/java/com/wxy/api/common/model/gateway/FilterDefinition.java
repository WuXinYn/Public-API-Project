package com.wxy.api.common.model.gateway;

import lombok.Data;

import java.util.Map;

@Data
public class FilterDefinition {
    private String name;
    private Map<String, String> args;
}