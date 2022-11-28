package com.tweats.controller;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.exceptions.*;
import com.tweats.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;
    @Mock
    private OrderService orderService;

    @Mock
    private Principal principal;


    @Test
    void shouldBeAbleToFetchAllCompletedOrdersWhenCategoryIdAndDateIsGiven() throws NoOrdersFoundException {
        long categoryId = 1;
        Date today = new Date();
        CompletedOrdersResponse expectedCompletedOrdersResponse = new CompletedOrdersResponse();
        when(orderService.getCompletedOrders(categoryId, today)).thenReturn(expectedCompletedOrdersResponse);

        CompletedOrdersResponse actualCompletedOrdersResponse = orderController.getCompletedOrders(categoryId, today);

        verify(orderService).getCompletedOrders(categoryId, today);
        assertThat(actualCompletedOrdersResponse, is(expectedCompletedOrdersResponse));
    }

    @Test
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() throws NoOrdersFoundException, UserNotFoundException, NoCategoryFoundException {
        ActiveOrderResponse expectedActiveOrderResponse = new ActiveOrderResponse();
        when(orderService.getActiveOrders(principal.getName())).thenReturn(expectedActiveOrderResponse);

        ActiveOrderResponse actualActiveOrderResponse = orderController.getActiveOrders(principal);

        verify(orderService).getActiveOrders(principal.getName());
        assertThat(actualActiveOrderResponse, is(expectedActiveOrderResponse));
    }

    @Test
    void shouldBeAbleToCompleteOrderWhenOrderIdIsGiven() throws OrderNotFoundException, OrderCategoryMismatchException, OrderCancelledException, UserNotFoundException, NoCategoryFoundException {
        long orderId = 1;

        orderController.completeTheOrder(principal, orderId);

        verify(orderService).completeTheOrder(principal.getName(), orderId);
    }

    @Test
    void shouldBeAbleToOrderOneItemWhenItemAndQuantityIsGiven() throws UserNotFoundException, ItemDoesNotExistException {
        long itemId = 1;
        long quantity = 3;

        orderController.orderAnItem(principal, itemId, quantity);

        verify(orderService).orderAnItem(principal.getName(), itemId, quantity);
    }

    @Test
    void shouldBeAbleToPlaceOrderWhenItemsArePresentInTheCart() throws UserNotFoundException, NoCategoryFoundException, EmptyCartException {
        long categoryId = 1;

        orderController.placeOrder(principal, categoryId);

        verify(orderService).placeOrder(principal.getName(), categoryId);
    }
}
