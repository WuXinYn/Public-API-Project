package com.api.apigetname.nettyclient.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 心跳消息类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatMessage
{
    private String clientId;
    private String message;

    @Override
    public String toString() {
        return "HeartbeatMessage{" +
                "clientId='" + clientId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
