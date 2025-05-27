package com.wxy.api.common.model.dto.notificationInfo;

import com.wxy.api.common.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationInfoQueryRequest extends PageRequest
{
    /**
     * 通知的唯一标识符
     */
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
     * 通知的状态
     * unread已读；read未读；treatment处理中；timeout失效；resolved已解决；error重大错误；warning警告，处理时长已经超过24h
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
     * 通知类型，
     * system系统通知；user用户通知；warning警告；error重大错误
     */
    private String type;

}
