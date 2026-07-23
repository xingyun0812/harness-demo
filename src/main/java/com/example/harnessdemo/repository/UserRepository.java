package com.example.harnessdemo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.harnessdemo.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 MyBatis-Plus Mapper.
 *
 * @author <your-name>
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
}
