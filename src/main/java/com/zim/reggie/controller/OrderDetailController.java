package com.zim.reggie.controller;

import com.zim.reggie.service.OrderDetailService;
import com.zim.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;
}
