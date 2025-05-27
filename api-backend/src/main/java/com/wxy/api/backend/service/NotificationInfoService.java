package com.wxy.api.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.api.common.model.entity.NotificationInfo;

/**
* @author DELL
* @description 针对表【notification_info(通知信息)】的数据库操作Service
* @createDate 2024-12-28 14:50:58
*/
public interface NotificationInfoService extends IService<NotificationInfo> {
    NotificationInfo generateWelcomeNotices(long userId);
}
