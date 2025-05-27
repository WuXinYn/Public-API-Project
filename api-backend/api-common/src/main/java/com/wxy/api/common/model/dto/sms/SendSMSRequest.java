package com.wxy.api.common.model.dto.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SendSMSRequest implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    @NonNull
    private String phone;

    @NonNull
    private String uuid;
}
