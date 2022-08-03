package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.common.CustomException;
import com.java.lightningfooddemo.dto.DishDto;
import com.java.lightningfooddemo.dto.SetmealDto;
import com.java.lightningfooddemo.entity.Dish;
import com.java.lightningfooddemo.entity.DishFlavor;
import com.java.lightningfooddemo.entity.Setmeal;
import com.java.lightningfooddemo.entity.SetmealDish;
import com.java.lightningfooddemo.mapper.SetmealMapper;
import com.java.lightningfooddemo.service.SetmealDishService;
import com.java.lightningfooddemo.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作setmeal 执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        //保存套餐和菜品的关联信息 操作setmeal_dish,执行insert
        setmealDishService.saveBatch(setmealDishes);
    }


    @Transactional
    public void removeWithDish(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        //查询状态 确定是否可以删除
        //select count(*) from setmeal where id in( list) and status = 1
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, list);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        long count = this.count(lambdaQueryWrapper);
        //如果不能删除 抛出一个异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //可以删除的话 先删除套餐中的数据
        this.removeByIds(list);
        //然后删除关联表中的数据
        //delete from setemeal_dish where setmeal_id in(lsit);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, list);

        setmealDishService.remove(queryWrapper);
    }

    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        //查询基本信息 查询表
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();


        //查询 套餐底下关联的菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDto);
            List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
            setmealDto.setSetmealDishes(list);

            return setmealDto;
        }
        return null;
    }

    @Transactional
    public void sureupdate(SetmealDto setmealDto) {
        //更新setmeal表基本信息
        this.updateById(setmealDto);
        //清理当前套餐对应数据 detele
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        //添加当前提交过来的数据 insert
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);

    }
}
