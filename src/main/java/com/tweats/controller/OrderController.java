package com.tweats.controller;

import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.Date;
@Controller
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;
    public void getCompletedOrders(long categoryId, Date date) throws NoOrdersFoundException {
        orderService.getCompletedOrders(categoryId, date);
    }
}
