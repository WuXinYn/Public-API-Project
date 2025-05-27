package com.wxy.api.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知信息
 * @TableName notification_info
 */
@TableName(value ="notification_info")
@Data
public class NotificationInfo implements Serializable {
    /**
     * 通知的唯一标识符
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收该通知的用户
     */
    private Long user_id;

    /**
     * 通知的标题
     */
    private String title;

    /**
     * 通知的详细内容
     */
    private String content;

    /**
     * 通知的状态（例如“已读”或“未读”）
     */
    private String status;

    /**
     * 通知的创建时间
     */
    private Date created_at;

    /**
     * 通知的更新时间
     */
    private Date updated_at;

    /**
     * 通知类型，'system' 为系统通知，'user' 为用户通知
     */
    private String type;

    /**
     * 软删除标志，0表示未删除，1表示已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}