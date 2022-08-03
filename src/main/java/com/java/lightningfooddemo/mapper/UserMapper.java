package com.java.lightningfooddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.lightningfooddemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
