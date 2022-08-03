package com.java.lightningfooddemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.java.lightningfooddemo.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
