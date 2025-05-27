package com.wxy.api.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.api.backend.mapper.NotificationInfoMapper;
import com.wxy.api.backend.service.NotificationInfoService;
import com.wxy.api.common.model.entity.NotificationInfo;
import com.wxy.api.common.model.enums.NotificationInfoEnum;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【notification_info(通知信息)】的数据库操作Service实现
* @createDate 2024-12-28 14:50:58
*/
@Service
public class NotificationInfoServiceImpl extends ServiceImpl<NotificationInfoMapper, NotificationInfo>
        implements NotificationInfoService
{

    /**
     * 欢迎新用户通知
     */
    @Override
    public NotificationInfo generateWelcomeNotices(long userId){
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setUser_id(userId);
        notificationInfo.setTitle("你好～♪");
        notificationInfo.setContent("你好! 新的一天，从一场美妙的邂逅开始。\n" +
                                            "欢迎使用API平台!\n" +
                                            "这束鲜花，要心怀感激的收下哦～♪");
        notificationInfo.setStatus(NotificationInfoEnum.Status_Unread.getValue());
        notificationInfo.setType(NotificationInfoEnum.Type_User.getValue());
        return notificationInfo;
    }
}




