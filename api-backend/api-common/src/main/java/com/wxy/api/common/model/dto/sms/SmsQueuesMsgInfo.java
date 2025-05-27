package com.wxy.api.common.model.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SmsQueuesMsgInfo
{
    private String account;

    private Integer authCode;

    private String current;
}
