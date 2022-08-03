package com.java.lightningfooddemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.java.lightningfooddemo.dto.DishDto;
import com.java.lightningfooddemo.entity.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {
    //新增菜品 同时插入胃口数据 需要操作两张表 dish dish_flavor
    public void saveWithFalvor(DishDto dishDto);

    //删除菜品 并判断逻辑
    public void removeDish(Long[] ids);

    //根据id获取DishDto 和 口味
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品
    public void sureupdate(DishDto dishDto);
}
