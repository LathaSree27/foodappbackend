package com.tweats.controller;

import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.Date;

import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderService orderService;

    private long categoryId;
    private Principal principal;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
        categoryId = 1;
    }

    @Test
    void shouldBeAbleToFetchAllCompletedOrdersWhenCategoryIdAndDateIsGiven() throws NoOrdersFoundException {
        Date today = new Date();

        orderController.getCompletedOrders(categoryId, today);

        verify(orderService).getCompletedOrders(categoryId, today);
    }

    @Test
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() throws NoOrdersFoundException {
        String vendorEmail = "abc@example.com";
        when(principal.getName()).thenReturn(vendorEmail);

        orderController.getActiveOrders(principal);

        verify(orderService).getActiveOrders(vendorEmail);
    }
}
