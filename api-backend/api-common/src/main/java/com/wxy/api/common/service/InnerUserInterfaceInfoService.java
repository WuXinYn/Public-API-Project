package com.wxy.api.common.service;

/**
* @author DELL
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-07-08 15:07:29
*/
public interface InnerUserInterfaceInfoService {
    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId,long userId);

    /**
     * 查询用户是否还有调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean checkLeftNum(long interfaceInfoId, long userId);
}
