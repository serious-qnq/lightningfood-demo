package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.common.CustomException;
import com.java.lightningfooddemo.entity.Category;
import com.java.lightningfooddemo.entity.Dish;
import com.java.lightningfooddemo.entity.Setmeal;
import com.java.lightningfooddemo.mapper.CategoryMapper;
import com.java.lightningfooddemo.service.CategoryService;
import com.java.lightningfooddemo.service.DishService;
import com.java.lightningfooddemo.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override//根据id删除分类 进行判断
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishlambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long dishcount = dishService.count(dishlambdaQueryWrapper);
        //查看分类是否关联了菜品 如果关联 抛出异常
        if (dishcount > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmeallambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmeallambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long setmealcount = setmealService.count(setmeallambdaQueryWrapper);

        //查看分类是否关联了套餐 如果关联 抛出异常
        if (setmealcount > 0) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(id);
    }
}
