package com.wxy.api.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.api.backend.mapper.UserInterfaceInfoMapper;
import com.wxy.api.backend.service.PayService;
import com.wxy.api.backend.service.UserInterfaceInfoService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import com.wxy.api.common.model.entity.UserOrderInfo;
import com.wxy.api.common.model.enums.InterfaceInfoStatusEnum;
import com.wxy.api.common.service.InnerRedisService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import java.util.UUID;

import static com.wxy.api.common.model.enums.UserInterfaceInfoEnum.FORBIDDEN;
import static com.wxy.api.common.model.enums.UserInterfaceInfoEnum.NORMAL;

/**
* @author DELL
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-07-08 15:07:29
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService
{

    @DubboReference
    private InnerRedisService innerRedisService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;


    /**
     * 校验
     *
     * @param userInterfaceInfo 用户调用接口信息
     * @param add 是否需要校验所有参数非空(创建时)
     */
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或请求用户不存在");
            }
        }

        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"剩余次数不能小于0");
        }
        if (userInterfaceInfo.getTotalNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"总次数不能小于0");
        }

    }

    /**
     * 检查接口剩余可调用次数
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 剩余次数>0 - True，否则False
     */
    @Override
//    @Cacheable(value = "invoke_api_leftNum")
    public boolean checkLeftNum(long interfaceInfoId, long userId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return userInterfaceInfo.getLeftNum() > 0;
    }

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 成功-True，失败-False
     */
    @Override
//    @CachePut(value = "")
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 校验
        if (interfaceInfoId < 0 || userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询是否存在数据
        if(this.checkIsNewUser(interfaceInfoId, userId) == null) {
            this.addNewDetail(interfaceInfoId, userId);
        }

        boolean update;
        String lockValue = UUID.randomUUID().toString(); // 唯一标识当前线程
        // redis事务加锁
        try {
            innerRedisService.tryLock(interfaceInfoId, userId, lockValue);
            // 查询当前剩余次数是否>0
            if(Boolean.FALSE.equals(checkLeftNum(interfaceInfoId, userId)) ) {
                return false;
            }
            // 更新计数
            UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("interfaceInfoId",interfaceInfoId);
            updateWrapper.eq("userId",userId);
            updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1"); // 更新条件
            update = this.update(updateWrapper);
        }
        finally {
            innerRedisService.releaseLock(interfaceInfoId, userId, lockValue);
        }

        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用接口统计事务失败");
        }
        return true;
    }

    /**
     * 判断是否是新数据
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 是新数据-True，否则False
     */
    @Override
    public UserInterfaceInfo checkIsNewUser(long interfaceInfoId, long userId){
        if (interfaceInfoId < 0 || userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        return this.getOne(queryWrapper);
    }

    /**
     * 新增记录
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     */
    @Override
    public boolean addNewDetail(long interfaceInfoId, long userId){
        if (interfaceInfoId < 0 || userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo newUserInterfaceInfo = new UserInterfaceInfo();
        newUserInterfaceInfo.setUserId(userId);
        newUserInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
        boolean temp = this.save(newUserInterfaceInfo);
        if (!temp) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增记录事务失败");
        }
        return true;
    }

    /**
     * 增加剩余可调用次数
     */
    @Override
    public boolean addLeftNum(long id, int num)
    {
        if (id < 0 || num <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectById(id);

        if (userInterfaceInfo.getStatus() == FORBIDDEN.getValue() && userInterfaceInfo.getLeftNum() + num > 0) {
            updateWrapper.set("status", NORMAL.getValue());
        }

        updateWrapper.setSql("leftNum = leftNum + " + num); // 更新条件
        return this.update(updateWrapper);
    }

    /**
     * 购买加油包成功后更新数据
     *
     * @param order 订单信息
     */
    @Override
    public void updatePayUserInterfaceInfo(UserOrderInfo order) {

        // 1. 查询用户调用接口信息（用户资源）
        long userId = order.getUserId();
        long interfaceId = order.getInterfaceId();
        int amount = order.getSetMenuNumber();
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);

        // 2. 如果原来是禁用状态，加完次数后如果>0，就恢复状态为正常
        int leftNum = userInterfaceInfo.getLeftNum(); // 剩余次数
        userInterfaceInfo.setLeftNum(leftNum + amount); // 更新剩余次数
        if (leftNum <= 0 && userInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.FORBIDDEN.getValue() && userInterfaceInfo.getLeftNum() > 0){
            userInterfaceInfo.setStatus(InterfaceInfoStatusEnum.NORMAL.getValue());
        }

        // 3. 更新用户资源
        boolean update = this.updateById(userInterfaceInfo);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "购买成功更新订单数据-更新用户资源失败");
        }
    }

    /**
     * 退款后更新数据
     *
     * @param order 订单信息
     */
    @Override
    public void updateRefundUserInterfaceInfo(UserOrderInfo order){

        // 1. 查询用户调用接口信息（用户资源）
        long userId = order.getUserId();
        long interfaceId = order.getInterfaceId();
        Integer amount = order.getSetMenuNumber();

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);

        // 2. 如果原来是正常状态，加完次数后如果<=0，就恢复状态为禁用
        int leftNum = userInterfaceInfo.getLeftNum(); // 剩余次数
        userInterfaceInfo.setLeftNum(leftNum - amount); // 更新剩余次数
        if (leftNum > 0 && userInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.NORMAL.getValue() && userInterfaceInfo.getLeftNum() <= 0){
            userInterfaceInfo.setStatus(InterfaceInfoStatusEnum.FORBIDDEN.getValue());
        }

        // 3. 更新用户资源
        boolean update = this.updateById(userInterfaceInfo);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "退款成功更新订单数据-更新用户资源失败");
        }
    }
}




