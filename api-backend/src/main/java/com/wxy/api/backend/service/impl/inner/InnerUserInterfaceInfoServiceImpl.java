package com.wxy.api.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wxy.api.backend.service.UserInterfaceInfoService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import com.wxy.api.common.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;

/**
 * 用户调用接口信息Service
 *
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService
{

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 成功-True，失败-False
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    /**
     * 查看剩余次数
     * 若没有次数了，则将用户接口表的对应记录的状态改为禁用
     *
     * @param interfaceInfoId 接口id
     * @param userId          用户id
     */
    @Override
    public boolean checkLeftNum(long interfaceInfoId, long userId)
    {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (userInterfaceInfo.getLeftNum() <= 0 && userInterfaceInfo.getStatus() != 1) {
            UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("interfaceInfoId", interfaceInfoId);
            updateWrapper.eq("userId", userId);
            updateWrapper.set("status", 1);
            return userInterfaceInfoService.update(updateWrapper);
        }
        return true;
    }

}
