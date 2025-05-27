package com.wxy.api.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.api.common.model.dto.userinterfaceinfo.UserInterfaceInfoAndNameResponse;
import com.wxy.api.common.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author DELL
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
 * @createDate 2024-07-08 15:07:28
 * @Entity com.yupi.project.model.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo>
{

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

    @Select("SELECT u.*, i.name, i.description, i.url " +
            "FROM user_interface_info u " +
            "LEFT JOIN interface_info i ON u.interfaceInfoId = i.id " +
            "WHERE u.userId = #{userId}")
    Page<UserInterfaceInfoAndNameResponse> getUserInterfaceInfoAndNameResponseList(Page<?> page, @Param("userId") long userId);

    @Select("SELECT u.*, i.name, i.description, i.url " +
            "FROM user_interface_info u " +
            "LEFT JOIN interface_info i ON u.interfaceInfoId = i.id ")
    Page<UserInterfaceInfoAndNameResponse> getUserInterfaceInfoResponseList(Page<?> page);
}



