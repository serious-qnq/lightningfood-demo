package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.entity.ShoppingCart;
import com.java.lightningfooddemo.mapper.ShoppingCartMapper;
import com.java.lightningfooddemo.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCarServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
