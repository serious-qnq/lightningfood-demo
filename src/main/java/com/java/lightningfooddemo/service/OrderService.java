package com.java.lightningfooddemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.java.lightningfooddemo.entity.OrderDetail;
import com.java.lightningfooddemo.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);

    public List<OrderDetail> getOrderDetailListByorderId(Long orderId);
}
