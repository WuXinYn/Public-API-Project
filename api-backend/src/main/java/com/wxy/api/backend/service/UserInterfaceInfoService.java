package com.wxy.api.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.api.common.model.dto.userorderinfo.ZfbPayCallBackMsg;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import com.wxy.api.common.model.entity.UserOrderInfo;

/**
* @author DELL
* &#064;description  针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* &#064;createDate  2024-07-08 15:07:29
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验
     *
     * @param userInterfaceInfo 用户调用接口信息
     * @param add 是否需要校验所有参数非空
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) ;

    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 成功-True，失败-False
     */
    boolean invokeCount(long interfaceInfoId,long userId);

    /**
     * 检查接口剩余可调用次数
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 剩余次数>0 - True，否则False
     */
    boolean checkLeftNum(long interfaceInfoId, long userId);

    /**
     * 判断是否是新数据
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return 是新用户-True，否则False
     */
    UserInterfaceInfo checkIsNewUser(long interfaceInfoId, long userId);

    /**
     * 新增记录
     *
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     */
    boolean addNewDetail(long interfaceInfoId, long userId);

    /**
     * 增加剩余可调用次数
     */
    boolean addLeftNum(long id, int num);

    /**
     * 购买加油包成功后更新用户资源
     *
     * @param order 订单信息
     */
    void updatePayUserInterfaceInfo(UserOrderInfo order);

    /**
     * 退款后更新用户资源
     *
     * @param order 订单信息
     */
    void updateRefundUserInterfaceInfo(UserOrderInfo order);
}
