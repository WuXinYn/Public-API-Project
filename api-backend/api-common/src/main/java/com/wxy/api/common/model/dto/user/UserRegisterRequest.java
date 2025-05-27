package com.wxy.api.common.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    @NonNull
    private String userAccount;

    @NonNull
    private String userPassword;

    @NonNull
    private String checkPassword;

}
