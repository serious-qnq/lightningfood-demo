package com.java.lightningfooddemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.lightningfooddemo.common.R;
import com.java.lightningfooddemo.dto.DishDto;
import com.java.lightningfooddemo.entity.Category;
import com.java.lightningfooddemo.entity.Dish;
import com.java.lightningfooddemo.entity.DishFlavor;
import com.java.lightningfooddemo.entity.Employee;
import com.java.lightningfooddemo.service.CategoryService;
import com.java.lightningfooddemo.service.DishFlavorService;
import com.java.lightningfooddemo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping//新增菜品
    public R<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFalvor(dishDto);
        //清理某个分类的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }


    @PutMapping//确定修改
    public R<String> sureupdate(@RequestBody DishDto dishDto) {
        dishService.sureupdate(dishDto);

        //清理所有菜品的缓存数据
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //清理某个分类的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }


    @GetMapping("/page") //分页查询
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.like(name != null, Dish::getName, name);

        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();
        for (Dish record : records) {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);

            Long categoryId = record.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//分类对象
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            list.add(dishDto);
        }

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }


    @GetMapping("/{id}") //根据id 回显
    public R<DishDto> update(@PathVariable Long id) {
        log.info("需要修改菜品的id{}", id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    @PostMapping("/status/{status}")//停售起售
    public R<String> updateStatusById(@PathVariable Integer status, Long[] ids) {
        log.info("根据id修改菜品的状态{},id为{}", status, ids);
        List<Long> list = Arrays.asList(ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.set(Dish::getStatus, status).in(Dish::getId, list);
        dishService.update(updateWrapper);
        return R.success("菜品状态修改成功");
    }


    @DeleteMapping//删除菜品
    public R<String> deleteCategory(Long[] ids) {
        List<Long> list = Arrays.asList(ids);

        dishService.removeDish(ids);

        return R.success("菜品删除成功");
    }


//    @GetMapping("/list")//根据菜品类型回显对应菜品数据
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")//根据菜品类型回显对应菜品数据
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = new ArrayList<>();
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_3132r24
        //从redis 中获取缓存数据
        Object o = redisTemplate.opsForValue().get(key);
        if (o != null) {
            dishDtoList = (List<DishDto>) o;
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);


        for (Dish record : list) {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);

            Long categoryId = record.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//分类对象
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = record.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper();
            wrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> flavorList = dishFlavorService.list(wrapper);
            dishDto.setFlavors(flavorList);
            dishDtoList.add(dishDto);
        }
        //如果不存在 需要查询数据库 把数据放进redis里
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }


}
