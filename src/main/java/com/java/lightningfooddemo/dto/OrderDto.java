package com.java.lightningfooddemo.dto;

import com.java.lightningfooddemo.entity.OrderDetail;
import com.java.lightningfooddemo.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;

}
