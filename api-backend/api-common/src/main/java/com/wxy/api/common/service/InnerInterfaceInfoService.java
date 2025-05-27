package com.wxy.api.common.service;

import com.wxy.api.common.model.entity.InterfaceInfo;

import java.util.List;

/**
 *
*/
public interface InnerInterfaceInfoService {

    /**
     * 查询接口是否存在
     */
    InterfaceInfo getInterfaceInfo(String url, String method);

    /**
     * 查询所有接口信息
     */
    List<InterfaceInfo> getAllInterfaceInfoInfo();

    /**
     * 下线异常接口
     */
     boolean offlineInterface(long interfaceId);

    /**
     * 给接口状态修改为正常
     */
     boolean onlineInterface(long interfaceId);

    /**
     * 判断接口是否在线
     *
     * @param item 接口信息
     * @return 接口在线-True，否则-False
     */
    boolean isInterfaceOnline(InterfaceInfo item);
}
