package com.tweats.controller;

import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderService orderService;
    private long categoryId;

    @BeforeEach
    void setUp() {
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
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() {
        orderController.getActiveOrders(categoryId);

        verify(orderService).getActiveOrders(categoryId);
    }
}