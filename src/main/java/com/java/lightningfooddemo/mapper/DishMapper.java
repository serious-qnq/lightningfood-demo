package com.java.lightningfooddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.lightningfooddemo.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
