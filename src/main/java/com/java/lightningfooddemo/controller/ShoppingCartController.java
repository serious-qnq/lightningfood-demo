package com.java.lightningfooddemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.java.lightningfooddemo.common.BaseContextUtils;
import com.java.lightningfooddemo.common.R;
import com.java.lightningfooddemo.entity.ShoppingCart;
import com.java.lightningfooddemo.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.namespace.QName;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingcart:{}", shoppingCart);
        //根据id 指定当前哪个用户的购物车数据
        Long currentId = BaseContextUtils.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前数据或者套餐中是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //是菜品
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //是套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        //如果已经存在 查看口味是否相同 相同数量+1
        if (cart != null) {
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(cart);
        } else {
            //如果不存在或者不相同 就添加到购物车 默认数量1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }
        return R.success(cart);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("接收到要删除订单的菜品id{}", shoppingCart);
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {     //菜品
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {//套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        if (cart.getNumber() > 1) {
            Integer number = cart.getNumber();
            cart.setNumber(number - 1);
            cart.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(cart);
        } else {
            shoppingCartService.removeById(cart);
        }
        return R.success(cart);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContextUtils.getCurrentId());

        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);

    }


    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContextUtils.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success("清空成功");
    }


}
