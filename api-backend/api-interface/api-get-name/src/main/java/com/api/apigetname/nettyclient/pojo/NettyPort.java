package com.api.apigetname.nettyclient.pojo;

import lombok.Getter;

@Getter
public enum NettyPort
{
    Gateway_Netty_Server_Port(8100, "Gateway_Netty_Server_Port"),
    Gateway_Netty_Server_Host(0, "127.0.0.1");

    private final int value;

    private final String content;

    NettyPort(int value, String content)
    {
        this.value = value;
        this.content = content;
    }

}
