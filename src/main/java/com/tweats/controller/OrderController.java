package com.tweats.controller;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("order")
public class OrderController {

    private OrderService orderService;

    @GetMapping("completed")
    public CompletedOrdersResponse getCompletedOrders(
            @RequestParam(name = "categoryId") long categoryId,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) throws NoOrdersFoundException {
        return orderService.getCompletedOrders(categoryId, date);
    }

    @GetMapping("active")
    public ActiveOrderResponse getActiveOrders(Principal principal) throws NoOrdersFoundException {
        String vendorEmail = principal.getName();
        return orderService.getActiveOrders(vendorEmail);
    }

    public void completeTheOrder(Principal principal, long orderId) {
        String vendorEmail = principal.getName();
        orderService.completeTheOrder(vendorEmail,orderId);
    }
}
