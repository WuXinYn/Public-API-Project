package com.wxy.api.common.model.dto.notificationInfo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationInfoUpdateStatusRequest implements Serializable
{
    /**
     * 通知的唯一标识符
     */
    @NonNull
    private Long id;
}
