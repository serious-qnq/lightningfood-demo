package com.java.lightningfooddemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.lightningfooddemo.common.BaseContextUtils;
import com.java.lightningfooddemo.common.R;
import com.java.lightningfooddemo.dto.OrderDto;
import com.java.lightningfooddemo.entity.OrderDetail;
import com.java.lightningfooddemo.entity.Orders;
import com.java.lightningfooddemo.service.OrderDetailService;
import com.java.lightningfooddemo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);

        return R.success("订单提交成功");
    }

    @GetMapping("/userPage")
    public R<Page> list(Long page, Long pageSize) {
        log.info("外卖查询订单page:{},pageSize:{}", page, pageSize);
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageDto = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, BaseContextUtils.getCurrentId());
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, wrapper);

        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (Orders record : records) {
            OrderDto orderDto = new OrderDto();
            Long orderId = record.getId();
            List<OrderDetail> orderDetails = orderService.getOrderDetailListByorderId(orderId);
            BeanUtils.copyProperties(record, orderDto);
            orderDto.setOrderDetails(orderDetails);
            orderDtoList.add(orderDto);
        }

        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        pageDto.setRecords(orderDtoList);

        return R.success(pageDto);
    }

    @GetMapping("/page")
    public R<Page> page(Long page, Long pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(number != null, Orders::getNumber, number)
                .gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        wrapper.orderByDesc(Orders::getCheckoutTime);
        orderService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> orderStatusChange(@RequestBody Map<String, String> map) {
        log.info("map:{}", map);
        Long orderId = Long.parseLong(map.get("id"));
        Integer status = Integer.parseInt(map.get("status"));
        if (orderId == null || status == null) {
            return R.error("传入信息不合法");
        }
        Orders order = orderService.getById(orderId);
        order.setStatus(status);
        orderService.updateById(order);
        return R.success("订单状态修改完成");
    }
}
