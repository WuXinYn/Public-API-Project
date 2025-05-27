package com.wxy.api.common.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HeaderNameType
{
    Access_Key("accessKey"),
    Nonce("nonce"),
    Timestamp("timestamp"),
    Sign("sign"),
    Interface_Host("interface_host"),
    Body("X-body"),
    Temp_Body("body"),
    Staining_Header("staining_header");

    private final String headerName;

    HeaderNameType(String headerName) {
        this.headerName = headerName;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getHeaderNames() {
        return Arrays.stream(values()).map(item -> item.headerName).collect(Collectors.toList());
    }

    public String getHeaderName() {
        return headerName;
    }
}
