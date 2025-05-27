package com.wxy.api.common.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户创建请求
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    @NonNull
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户角色: user, admin
     */
    @NonNull
    private String userRole;

    /**
     * 密码
     */
    @NonNull
    private String userPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}