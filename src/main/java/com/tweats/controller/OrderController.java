package com.tweats.controller;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.exceptions.*;
import com.tweats.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("order")
@Validated
public class OrderController {

    private OrderService orderService;

    @GetMapping("completed")
    public CompletedOrdersResponse getCompletedOrders(
            @RequestParam(name = "categoryId") long categoryId,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) throws NoOrdersFoundException {
        return orderService.getCompletedOrders(categoryId, date);
    }

    @GetMapping("active")
    public ActiveOrderResponse getActiveOrders(Principal principal) throws NoOrdersFoundException, UserNotFoundException, NoCategoryFoundException {
        String vendorEmail = principal.getName();
        return orderService.getActiveOrders(vendorEmail);
    }

    @PutMapping("complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeTheOrder(Principal principal,
                                 @RequestParam(name = "orderId") long orderId) throws OrderNotFoundException, OrderCategoryMismatchException, OrderCancelledException, UserNotFoundException, NoCategoryFoundException {
        String vendorEmail = principal.getName();
        orderService.completeTheOrder(vendorEmail, orderId);
    }

    @PostMapping("buy/{itemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void orderAnItem(Principal principal,
                            @PathVariable(name = "itemId") long itemId,
                            @RequestParam(name = "quantity") @Min(value = 1,message = "Quantity can't be less than 1") long quantity) throws UserNotFoundException, ItemDoesNotExistException {
        orderService.orderAnItem(principal.getName(), itemId, quantity);
    }

    public void placeOrder(Principal principal, long categoryId) throws UserNotFoundException, NoCategoryFoundException {
        orderService.placeOrder(principal.getName(), categoryId);
    }
}
