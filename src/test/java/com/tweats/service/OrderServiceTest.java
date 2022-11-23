package com.tweats.service;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.controller.response.OrderResponse;
import com.tweats.controller.response.OrderedItemResponse;
import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.exceptions.OrderNotFoundException;
import com.tweats.model.*;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService orderService;
    private Order order;
    private Category category;
    private OrderRepository orderRepository;
    private Image image;
    private ImageService imageService;
    private UserPrincipalService userPrincipalService;
    private CategoryRepository categoryRepository;
    private User user;

    @BeforeEach
    void setUp() {
        userPrincipalService = mock(UserPrincipalService.class);
        categoryRepository = mock(CategoryRepository.class);
        imageService = mock(ImageService.class);
        orderRepository = mock(OrderRepository.class);
        order = mock(Order.class);
        category = mock(Category.class);
        image = mock(Image.class);
        user = mock(User.class);
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        orderService = new OrderService(orderRepository, imageService, userPrincipalService, categoryRepository);

    }


    @Test
    void shouldBeAbleToFetchAllCompletedOrdersWhenCategoryIdAndDateIsGiven() throws NoOrdersFoundException {
        Date today = new Date();
        int count = 1;
        String itemName = "Mango";
        BigDecimal revenue = new BigDecimal(100);
        BigDecimal billAmount = new BigDecimal(100);
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        ArrayList<OrderedItemResponse> orderedItemResponses = new ArrayList<>();
        long quantity = 2;
        BigDecimal itemPrice = new BigDecimal(50);
        Item item = new Item(itemName, image, itemPrice, category);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        when(orderRepository.getAllOrdersByCategoryDateAndStatus(category.getId(), today, true)).thenReturn(orders);
        when(orderRepository.getRevenueOfCompletedOrdersByCategoryIdAndDate(category.getId(), today)).thenReturn(revenue);
        HashSet<OrderedItem> orderedItems = new HashSet<>();
        OrderedItem orderedItem = new OrderedItem(order, item, quantity);
        orderedItems.add(orderedItem);
        when(order.getOrderedItems()).thenReturn(orderedItems);
        String imageLink = "http://localhost:8080/tweats/api/v1/images/" + item.getImage().getId();
        when(imageService.getImageLink(image)).thenReturn(imageLink);
        when(order.getDate()).thenReturn(today);
        orderedItemResponses.add(new OrderedItemResponse(item.getId(), item.getName(), quantity, itemPrice, imageLink));
        orderResponses.add(new OrderResponse(order.getId(), today, billAmount, orderedItemResponses));
        CompletedOrdersResponse expectedCompletedOrdersResponse = new CompletedOrdersResponse(count, revenue, orderResponses);

        CompletedOrdersResponse actualCompletedOrdersResponse = orderService.getCompletedOrders(category.getId(), today);

        verify(orderRepository).getAllOrdersByCategoryDateAndStatus(category.getId(), today, true);
        assertThat(actualCompletedOrdersResponse, is(expectedCompletedOrdersResponse));

    }

    @Test
    void shouldThrowNoOrdersFoundExceptionWhenThereAreNoCompletedOrders() {
        long categoryId = category.getId();
        Date today = new Date();

        assertThrows(NoOrdersFoundException.class, () -> orderService.getCompletedOrders(categoryId, today));
    }

    @Test
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() throws NoOrdersFoundException {
        OrderService mockedOrderService = spy(orderService);
        Date today = new Date();
        int count = 1;
        BigDecimal billAmount = new BigDecimal(100);
        String itemName = "Mango";
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        ArrayList<OrderedItemResponse> orderedItemResponses = new ArrayList<>();
        long quantity = 2;
        BigDecimal itemPrice = new BigDecimal(50);
        Item item = new Item(itemName, image, itemPrice, category);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        when(mockedOrderService.getCurrentDate()).thenReturn(today);
        when(orderRepository.getAllOrdersByCategoryDateAndStatus(category.getId(), today, false)).thenReturn(orders);
        HashSet<OrderedItem> orderedItems = new HashSet<>();
        OrderedItem orderedItem = new OrderedItem(order, item, quantity);
        orderedItems.add(orderedItem);
        when(order.getOrderedItems()).thenReturn(orderedItems);
        String imageLink = "http://localhost:8080/tweats/api/v1/images/" + item.getImage().getId();
        when(imageService.getImageLink(image)).thenReturn(imageLink);
        when(order.getDate()).thenReturn(today);
        orderedItemResponses.add(new OrderedItemResponse(item.getId(), item.getName(), quantity, itemPrice, imageLink));
        orderResponses.add(new OrderResponse(order.getId(), today, billAmount, orderedItemResponses));
        ActiveOrderResponse expectedActiveOrderResponse = new ActiveOrderResponse(count, orderResponses);

        ActiveOrderResponse actualActiveOrders = mockedOrderService.getActiveOrders(user.getEmail());

        verify(orderRepository).getAllOrdersByCategoryDateAndStatus(category.getId(), today, false);
        assertThat(actualActiveOrders, is(expectedActiveOrderResponse));
    }

    @Test
    void shouldThrowNoOrderFoundExceptionWhenThereAreNoActiveOrdersFound() {
        assertThrows(NoOrdersFoundException.class, () -> orderService.getActiveOrders(user.getEmail()));
    }

    @Test
    void shouldCompleteTheOrderWhenOrderIsNotDelivered() throws OrderNotFoundException {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        order.setDelivered(true);

        orderService.completeTheOrder(user.getEmail(), order.getId());

        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenInvalidOrderIdIsGiven() {
        assertThrows(OrderNotFoundException.class,()->orderService.completeTheOrder(user.getEmail(), order.getId()));
    }
}
