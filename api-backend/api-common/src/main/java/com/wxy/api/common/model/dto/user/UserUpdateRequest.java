package com.wxy.api.common.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新请求
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserUpdateRequest implements Serializable {

    /**
     * id
     */
    @NonNull
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
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
     * 用户角色: user, admin, is_locked
     */
    private String userRole;

    /**
     * 密码
     */
    private String userPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}