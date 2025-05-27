package com.wxy.api.gateway.nettyserver.service;

import com.wxy.api.common.model.entity.InterfaceInfo;
import com.wxy.api.common.model.entity.NotificationInfo;
import com.wxy.api.common.model.enums.InterfaceInfoStatusEnum;
import com.wxy.api.common.service.InnerInterfaceInfoService;
import com.wxy.api.common.service.InnerNotificationInfoService;
import com.wxy.api.common.service.InnerRedisService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 自定义接口异常处理业务
 */
@Service
@NoArgsConstructor
@Slf4j
public class ExceptionService
{
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerNotificationInfoService innerNotificationInfoService;

    @DubboReference
    private InnerRedisService innerRedisService;

    public static final String status = "-status";
    public static final String Killed = "killed";
    public static final String Alive = "alive";
    public static final long redis_time = 60*24*2;

    /**
     * 接口连接异常处理（异步）
     *
     * @param cause
     */
    @Async // 走数据库，用异步处理
    public void handleException(Throwable cause, String channelID)
    {
        // 记录异常信息
        log.error("Asynchronously handling exception: {}", cause.getMessage(), cause);

        // 校验参数
        String message = this.validParamsIsNull(channelID);
        if (message == null) {
            log.error("channel :{} 的 数据丢失！！！", channelID);
            return;
        }

        // 拿到url, method
        int index = message.indexOf("\\");
        String url = message.substring(0, index);
        String method = message.substring(index + 1);

        // 查询接口
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, method);
        long interfaceId = interfaceInfo.getId();
        log.info("接口{}出现异常!!!", interfaceId);

        // 修改接口状态（Redis）
        this.setLastStatus(url, method, Killed);

        // 给接口状态修改为异常（MySQL）
        boolean interfaceOffline = innerInterfaceInfoService.offlineInterface(interfaceId);
        if (interfaceOffline) {
            log.info("成功将接口{}的状态设为异常！", interfaceId);
        }

        // 给管理员发送通知
        log.info("Notifying admin about exception: {}", cause.getMessage());
        NotificationInfo notificationInfo = innerNotificationInfoService.generatedApiKilledNotificationInfo(interfaceId, 0);
        boolean notification = innerNotificationInfoService.sendMessageToAdmins(notificationInfo);
        if (notification) {
            log.info("已通知管理员处理！");
        }

        log.error("接口异常业务处理完毕!\n");

        //recoverState(cause);  // 执行其他异常处理逻辑
    }

    /**
     * 恢复系统状态 todo
     *
     * @param cause
     */
    private void recoverState(Throwable cause)
    {
        log.info("Recovering state after exception...");
    }

    /**
     * 校验channelID对应的url和method
     *
     * @return
     */
    public String validParamsIsNull(String channelID)
    {
        String param = innerRedisService.findParam(channelID);
        if (param == null) {
            log.warn("Redis 内存储的 channel :{} 的 host或者method为空！", channelID);
        }
        return param;
    }

    /**
     * 根据channelID记录url、method
     */
    public void setMsg(String channelID, String message)
    {
        int urlBegin = message.indexOf("=") + 2;
        int urlEnd = message.indexOf("\\");
        String url = message.substring(urlBegin, urlEnd);
        String method = message.substring(urlEnd + 1);

        if (this.validParamsIsNull(channelID) == null) {
            log.info("初始设置: 记录 message: {}, from channel: {}\n", message, channelID);
            // 检查接口上一次的状态(是否掉过线)
            this.checkStatus(url, method);
        }
        else {
            log.info("form channel: {}, record message: {}\n", channelID, message);
        }

        innerRedisService.setParams(channelID, url + "\\" + method, 10); // 保存进程以及对应接口信息10min
    }

    /**
     * 检查是否掉过线
     */
    public void checkStatus(String url, String method){
        String lastStatus = this.getLastStatus(url, method);
        if (lastStatus != null){
            if (lastStatus.equals(Killed)) {
                this.recoverInterfaceStatus(url, method); // 恢复意外掉线的接口在MySQL数据库内的状态
            }
        }
        this.setLastStatus(url, method, Alive);
    }

    /**
     * 从Redis拿到上一次的状态
     */
    public String getLastStatus(String url, String method){
        String interfaceKey = url + "\\" + method + status;
        return innerRedisService.findParam(interfaceKey);
    }

    /**
     * 保存接口状态(保存在Redis内48h)
     */
    public void setLastStatus(String url, String method, String interfaceStatus){
        String interfaceKey = url + "\\" + method + status;
        innerRedisService.setParams(interfaceKey, interfaceStatus, redis_time);
    }

    /**
     * 恢复接口状态(走MySQL数据库)
     */
    public void recoverInterfaceStatus(String url, String method)
    {
        // 查询接口
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, method);
        long interfaceId = interfaceInfo.getId();

        // 给接口状态修改为正常
        if (!interfaceInfo.getStatus().equals(InterfaceInfoStatusEnum.ONLINE.getValue())) {
            boolean interfaceOffline = innerInterfaceInfoService.onlineInterface(interfaceId);
            if (interfaceOffline) {
                log.info("成功将接口{}的状态设为正常！", interfaceId);
            }
            // 生成接口状态恢复通知
            NotificationInfo notificationInfo = innerNotificationInfoService.generatedApiAliveNotificationInfo(interfaceId, 0);
            // 修改之前发过的接口通知状态为已解决
            innerNotificationInfoService.changeNotificationInfoStatus(interfaceId);
            // 给管理员发送接口状态恢复通知
            innerNotificationInfoService.sendMessageToAdmins(notificationInfo);
        }
    }
}
