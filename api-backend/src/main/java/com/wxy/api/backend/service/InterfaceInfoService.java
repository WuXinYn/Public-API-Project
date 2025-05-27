package com.wxy.api.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.api.common.model.entity.InterfaceInfo;

/**
* @author DELL
* &#064;description  针对表【interface_info(接口信息)】的数据库操作Service
* &#064;createDate  2024-06-22 14:08:51
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验参数
     *
     * @param interfaceInfo 接口id
     * @param add 是否需要校验全部参数
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) ;

}
