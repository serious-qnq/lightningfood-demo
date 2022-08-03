package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.entity.SetmealDish;
import com.java.lightningfooddemo.mapper.SetmealDishMapper;
import com.java.lightningfooddemo.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
