package com.wxy.api.common.model.dto.notificationInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建请求
 *
 * &#064;TableName  product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationInfoAddRequest implements Serializable {

    /**
     * 接收该通知的用户
     */
    @NonNull
    private Long user_id;

    /**
     * 通知的标题
     */
    @NonNull
    private String title;

    /**
     * 通知的详细内容
     */
    @NonNull
    private String content;

    /**
     * 通知类型
     * 'system' 系统通知；'user' 用户通知；'warning' 为警告；'error' 重大错误
     */
    @NonNull
    private String type;


    @Serial
    private static final long serialVersionUID = 1L;


}