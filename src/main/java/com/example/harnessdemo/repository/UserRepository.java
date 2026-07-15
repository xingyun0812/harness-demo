package com.example.harnessdemo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.harnessdemo.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRepository extends BaseMapper<User> {
}
