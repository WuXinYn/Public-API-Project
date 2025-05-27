package com.wxy.api.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wxy.api.common.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Entity com.yupi.project.model.domain.User
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT COUNT(*) FROM user WHERE userAccount = #{userAccount}")
    Long countAllUsers(@Param("userAccount") String userAccount);
}




