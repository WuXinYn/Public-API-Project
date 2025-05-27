package com.wxy.api.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequire<T>
{
    public String ip;
    public T params;
}
