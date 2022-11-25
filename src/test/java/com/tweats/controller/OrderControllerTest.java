package com.tweats.controller;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.exceptions.OrderCancelledException;
import com.tweats.exceptions.OrderCategoryMismatchException;
import com.tweats.exceptions.OrderNotFoundException;
import com.tweats.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        String vendorEmail = "abc@example.com";
        when(principal.getName()).thenReturn(vendorEmail);
    }

    @Test
    void shouldBeAbleToFetchAllCompletedOrdersWhenCategoryIdAndDateIsGiven() throws NoOrdersFoundException {
        Date today = new Date();
        CompletedOrdersResponse expectedCompletedOrdersResponse = new CompletedOrdersResponse();
        when(orderService.getCompletedOrders(categoryId, today)).thenReturn(expectedCompletedOrdersResponse);

        CompletedOrdersResponse actualCompletedOrdersResponse = orderController.getCompletedOrders(categoryId, today);

        verify(orderService).getCompletedOrders(categoryId, today);
        assertThat(actualCompletedOrdersResponse, is(expectedCompletedOrdersResponse));
    }

    @Test
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() throws NoOrdersFoundException {
        ActiveOrderResponse expectedActiveOrderResponse = new ActiveOrderResponse();
        when(orderService.getActiveOrders(principal.getName())).thenReturn(expectedActiveOrderResponse);

        ActiveOrderResponse actualActiveOrderResponse = orderController.getActiveOrders(principal);

        verify(orderService).getActiveOrders(principal.getName());
        assertThat(actualActiveOrderResponse, is(expectedActiveOrderResponse));
    }

    @Test
    void shouldBeAbleToCompleteOrderWhenOrderIdIsGiven() throws OrderNotFoundException, OrderCategoryMismatchException, OrderCancelledException {
        long orderId = 1;

        orderController.completeTheOrder(principal, orderId);

        verify(orderService).completeTheOrder(principal.getName(), orderId);
    }

    @Test
    void shouldBeAbleToOrderOneItemWhenItemAndQuantityIsGiven() {
        long itemId = 1;
        long quantity = 3;

        orderController.orderAnItem(principal, itemId, quantity);

        verify(orderService).orderAnItem(principal.getName(), itemId, quantity);
    }
}
