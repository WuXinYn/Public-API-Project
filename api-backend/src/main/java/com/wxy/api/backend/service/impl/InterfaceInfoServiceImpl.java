package com.wxy.api.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.api.backend.mapper.InterfaceInfoMapper;
import com.wxy.api.backend.service.InterfaceInfoService;
import com.wxy.api.common.common.BusinessException;
import com.wxy.api.common.common.ErrorCode;
import com.wxy.api.common.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


/**
* @author DELL
* &#064;description  针对表【interface_info(接口信息)】的数据库操作Service实现
* &#064;createDate  2024-06-22 14:08:51
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService
{

    /**
     * 校验参数
     *
     * @param interfaceInfo 接口id
     * @param add 是否需要校验全部参数
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//      插件：GenerateAllSetter, 在对象上alt+enter
        Long id = interfaceInfo.getId();
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String method = interfaceInfo.getMethod();
        Long userID = interfaceInfo.getUserID();

        // 参数校验
        if (add) { // 创建时，所有参数必须非空
            if (StringUtils.isAnyBlank(name,description,url,requestHeader,responseHeader,method)) { //几个值中任意一个为空（“” 和null）则为true；
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        else { // 更新时
            if (id == null || id < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        // 检查参数合理性
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称不能过长");
        }

    }

}




