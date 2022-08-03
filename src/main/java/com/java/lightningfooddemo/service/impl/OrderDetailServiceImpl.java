package com.java.lightningfooddemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java.lightningfooddemo.entity.OrderDetail;
import com.java.lightningfooddemo.mapper.OrderDetailMapper;
import com.java.lightningfooddemo.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
