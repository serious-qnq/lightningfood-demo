package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.entity.DishFlavor;
import com.java.lightningfooddemo.mapper.DishFlavorMapper;
import com.java.lightningfooddemo.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
