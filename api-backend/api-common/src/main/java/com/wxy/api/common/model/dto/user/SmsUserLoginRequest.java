package com.wxy.api.common.model.dto.user;

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
public class SmsUserLoginRequest implements Serializable
{
    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    @NonNull
    private String userAccount;

    @NonNull
    private String code;
}
