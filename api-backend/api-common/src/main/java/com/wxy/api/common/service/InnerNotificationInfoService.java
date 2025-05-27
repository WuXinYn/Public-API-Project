package com.wxy.api.common.service;

import com.wxy.api.common.model.entity.NotificationInfo;

/**
 * @author DELL
 * @description 针对表【notification_info(通知信息)】的数据库操作Service实现
 * @createDate 2024-12-28 14:50:58
 */
public interface InnerNotificationInfoService
{
    /**
     * 给管理员发通知
     */
    boolean sendMessageToAdmins(NotificationInfo notificationInfo);

    /**
     * 生成接口异常通知
     *
     * @param interfaceId 接口id
     * @param type
     * @return
     */
    NotificationInfo generatedApiKilledNotificationInfo(long interfaceId, int type);

    /**
     * 生成接口正常通知
     *
     * @param interfaceId
     * @param type
     * @return
     */
    NotificationInfo generatedApiAliveNotificationInfo(long interfaceId, int type);

    /**
     * 修改之前同一个接口发过的通知状态为已解决
     */
    void changeNotificationInfoStatus(long interfaceId);
}
