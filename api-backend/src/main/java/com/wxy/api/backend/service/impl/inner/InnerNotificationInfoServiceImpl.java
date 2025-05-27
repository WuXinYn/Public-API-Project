package com.wxy.api.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxy.api.backend.mapper.NotificationInfoMapper;
import com.wxy.api.backend.service.NotificationInfoService;
import com.wxy.api.common.model.entity.NotificationInfo;
import com.wxy.api.common.model.enums.NotificationInfoEnum;
import com.wxy.api.common.service.InnerNotificationInfoService;
import com.wxy.api.common.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import com.wxy.api.common.model.entity.User;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 内部项目调用的通知接口
 */
@DubboService
public class InnerNotificationInfoServiceImpl implements InnerNotificationInfoService
{
    @Resource
    private InnerUserService innerUserService;

    @Resource
    private NotificationInfoService notificationInfoService;

    @Resource
    private NotificationInfoMapper notificationInfoMapper;

    private static final long Day_Hours_Millis = 1000 * 60 * 60 * 24;

    /**
     * 给所有管理员发送通知
     *
     * @param notificationInfo 通知信息
     * @return 发送成功-True，否则-False
     */
    @Override
    public boolean sendMessageToAdmins(NotificationInfo notificationInfo)
    {
        // 查询该条消息过去24h是否已经发过了 todo
        NotificationInfo noInfo = notificationInfoMapper.selectById(notificationInfo.getId());
        if (noInfo != null) {
            long systemCurrentTime = System.currentTimeMillis(); // 系统时间
            long createdTime = noInfo.getCreated_at().getTime(); // 通知的首次创建时间
            if (systemCurrentTime - createdTime > Day_Hours_Millis) { // 发出通知后24仍未解决
                noInfo.setStatus(NotificationInfoEnum.Status_Warning.getValue());
            }
            else if (systemCurrentTime - createdTime > Day_Hours_Millis * 2) { // 发出通知后48h仍未解决
                noInfo.setStatus(NotificationInfoEnum.Status_Error.getValue());
            }
            noInfo.setUpdated_at(new Date(System.currentTimeMillis())); // 更新通知的更新时间
            return notificationInfoMapper.updateById(noInfo) == 1;
        }

        // 查询所有的管理员列表
        List<User> adminList = innerUserService.getAllAdmin();
        if (adminList == null) {
            return false;
        }
        // 如果id == 0，则向所有管理员发出通知
        if (notificationInfo.getUser_id() == 0) {
            for (User item : adminList) {
                NotificationInfo i = new NotificationInfo();
                BeanUtils.copyProperties(notificationInfo, i);
                i.setUser_id(item.getId());
                notificationInfoService.save(i);
            }
        }
        return true;
    }

    /**
     * 生成接口异常通知
     *
     * @param interfaceId 接口id
     * @param type
     * @return
     */
    @Override
    public NotificationInfo generatedApiKilledNotificationInfo(long interfaceId, int type)
    {
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setUser_id((long) type);
        notificationInfo.setTitle("接口" + interfaceId + "异常");
        notificationInfo.setContent("接口" + interfaceId + "出现异常，请管理员联系接口开发者，处理相关事宜，尽早安排重新上线接口！");
        notificationInfo.setStatus(NotificationInfoEnum.Status_Unread.getValue());
        notificationInfo.setType(NotificationInfoEnum.Type_System.getValue());
        return notificationInfo;
    }

    /**
     * 生成接口正常通知
     *
     * @param interfaceId
     * @param type
     * @return
     */
    @Override
    public NotificationInfo generatedApiAliveNotificationInfo(long interfaceId, int type)
    {
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setUser_id((long) type);
        notificationInfo.setTitle("接口恢复");
        notificationInfo.setContent("接口" + interfaceId + "因特殊原因出现异常，现已恢复正常，已重新上线！");
        notificationInfo.setStatus(NotificationInfoEnum.Status_Unread.getValue());
        notificationInfo.setType(NotificationInfoEnum.Type_System.getValue());
        return notificationInfo;
    }

    /**
     * 修改之前同一个接口发过的通知状态为已解决
     */
    @Override
    public void changeNotificationInfoStatus(long interfaceId)
    {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        updateWrapper.likeRight("content", "接口" + interfaceId);
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setStatus(NotificationInfoEnum.Status_Treatment.getValue());
        notificationInfoService.update(notificationInfo, updateWrapper);
    }
}
