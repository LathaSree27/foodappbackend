package com.tweats.controller;

import com.tweats.controller.response.OrdersResponse;
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

    @Mock
    private OrderService orderService;
    @Mock
    private Principal principal;
    @InjectMocks
    private OrderController orderController;

    @Test
    void shouldBeAbleToFetchAllCompletedOrdersWhenCategoryIdAndDateIsGiven() throws NoOrdersFoundException {
        long categoryId = 1;
        Date today = new Date();
        OrdersResponse expectedOrdersResponse = OrdersResponse.builder().build();
        when(orderService.getCompletedOrders(categoryId, today)).thenReturn(expectedOrdersResponse);

        OrdersResponse actualOrdersResponse = orderController.getCompletedOrders(categoryId, today);

        verify(orderService).getCompletedOrders(categoryId, today);
        assertThat(actualOrdersResponse, is(expectedOrdersResponse));
    }

    @Test
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() throws NoOrdersFoundException, UserNotFoundException, NoCategoryFoundException {
        OrdersResponse expectedActiveOrderResponse = OrdersResponse.builder().build();
        when(orderService.getActiveOrders(principal.getName())).thenReturn(expectedActiveOrderResponse);

        OrdersResponse actualActiveOrderResponse = orderController.getActiveOrders(principal);

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
