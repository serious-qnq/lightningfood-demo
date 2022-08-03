package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.common.CustomException;
import com.java.lightningfooddemo.dto.DishDto;
import com.java.lightningfooddemo.entity.Dish;
import com.java.lightningfooddemo.entity.DishFlavor;
import com.java.lightningfooddemo.entity.SetmealDish;
import com.java.lightningfooddemo.mapper.DishMapper;
import com.java.lightningfooddemo.service.DishFlavorService;
import com.java.lightningfooddemo.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional//新增菜品 并保存口味
    public void saveWithFalvor(DishDto dishDto) {
        //普通菜品
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        //口味
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional//删除菜品 并判断逻辑
    public void removeDish(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        //查询状态 确定是否可以删除
        //select count(*) from dish where id in( list) and status = 1
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, list);
        queryWrapper.eq(Dish::getStatus, 1);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("菜品正在售卖，不能删除");
        }
        //可以删除的话 先删除套餐中的数据
        this.removeByIds(list);
        //然后删除关联表中的数据
        //delete from DishFlavor where dish_id in(lsit);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DishFlavor::getDishId, list);

        dishFlavorService.remove(wrapper);
    }

    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询基本信息 dish表
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);


        //查询口味信息 dish_flavor表
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;

    }

    @Transactional
    public void sureupdate(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据 detele
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        //添加当前提交过来的口味数据 insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }


}
