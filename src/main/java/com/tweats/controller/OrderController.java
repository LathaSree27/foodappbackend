package com.tweats.controller;

import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
@RestController
@AllArgsConstructor
@RequestMapping("orders")
public class OrderController {

    private OrderService orderService;

    @GetMapping("completed")
    public void getCompletedOrders(@RequestParam(name = "categoryId") long categoryId, @RequestParam("date") Date date) throws NoOrdersFoundException {
        orderService.getCompletedOrders(categoryId, date);
    }
}
