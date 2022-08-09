package com.java.lightningfooddemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.lightningfooddemo.common.R;
import com.java.lightningfooddemo.dto.DishDto;
import com.java.lightningfooddemo.dto.SetmealDto;
import com.java.lightningfooddemo.entity.Category;
import com.java.lightningfooddemo.entity.Dish;
import com.java.lightningfooddemo.entity.Setmeal;
import com.java.lightningfooddemo.entity.SetmealDish;
import com.java.lightningfooddemo.service.CategoryService;
import com.java.lightningfooddemo.service.SetmealDishService;
import com.java.lightningfooddemo.service.SetmealService;
import com.sun.java_cup.internal.runtime.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page") //分页
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页的id{},分页的pagesize{},分页的name{}", page, pageSize, name);
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, lambdaQueryWrapper);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> dtoList = new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
                dtoList.add(setmealDto);
            }
        }
        setmealDtoPage.setRecords(dtoList);
        return R.success(setmealDtoPage);
    }


    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(Long[] ids) {
        log.info("获取需要删除套餐的ids{}", ids);
        setmealService.removeWithDish(ids);

        return R.success("套餐删除成功");
    }


    @PostMapping("/status/{status}")//停售起售
    public R<String> updateStatusById(@PathVariable Integer status, Long[] ids) {
        log.info("根据id修改菜品的状态{},id为{}", status, ids);
        List<Long> list = Arrays.asList(ids);
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.set(Setmeal::getStatus, status).in(Setmeal::getId, list);
        setmealService.update(updateWrapper);
        return R.success("菜品状态修改成功");
    }


    @GetMapping("/{id}") //根据id 回显
    public R<SetmealDto> update(@PathVariable Long id) {
        log.info("需要修改菜品的id{}", id);
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);

        return R.success(setmealDto);
    }


    @PutMapping//确定修改
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> Sureupdate(@RequestBody SetmealDto setmealDto) {
        setmealService.sureupdate(setmealDto);
        return R.success("套菜修改成功");
    }


    @GetMapping("/list")
    @Cacheable(value = "setmealCache" ,key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }

    @GetMapping("/dish/{id}") //点击菜品获取套餐数据
    public R<List<DishDto>> dish(@PathVariable("id") Long setmealId) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);

        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        List<DishDto> dishDtoList = new ArrayList<>();

        for (SetmealDish setmealDish : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(setmealDish, dishDto);

            Long dishId = setmealDish.getId();
            SetmealDish dish = setmealDishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);
            dishDtoList.add(dishDto);
        }


        return R.success(dishDtoList);
    }

}