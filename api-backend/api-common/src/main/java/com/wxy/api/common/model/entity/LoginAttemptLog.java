package com.wxy.api.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账号、IP封禁信息
 *
 * @TableName login_attempt_log
 */
@TableName(value ="login_attempt_log")
@Data
public class LoginAttemptLog implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户登录IP
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private Date attempt_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}