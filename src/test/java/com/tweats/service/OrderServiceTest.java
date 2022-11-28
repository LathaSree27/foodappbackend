package com.tweats.service;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.controller.response.OrderResponse;
import com.tweats.controller.response.OrderedItemResponse;
import com.tweats.exceptions.*;
import com.tweats.model.*;
import com.tweats.model.constants.OrderStatus;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ItemRepository;
import com.tweats.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private Item item;
    @InjectMocks
    private OrderService orderService;
    @Mock
    private Order order;
    @Mock
    private Category category;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private Image image;
    @Mock
    private ImageService imageService;
    @Mock
    private UserPrincipalService userPrincipalService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private User user;

    private void prepareData(int count, Date date, List<Order> orders, List<OrderResponse> orderResponses) {
        BigDecimal billAmount = new BigDecimal(100);
        String itemName = "Mango";
        HashSet<OrderedItem> orderedItems = new HashSet<>();
        ArrayList<OrderedItemResponse> orderedItemResponses = new ArrayList<>();
        long quantity = 2;
        BigDecimal itemPrice = new BigDecimal(50);
        String imageLink = "http://localhost:8080/tweats/api/v1/images/" + image.getId();

        Item item = new Item(itemName, image, itemPrice, category);
        orders.add(order);
        OrderedItem orderedItem = new OrderedItem(order, item, quantity);
        orderedItems.add(orderedItem);
        orderedItemResponses.add(new OrderedItemResponse(item.getId(), item.getName(), quantity, itemPrice, imageLink));
        orderResponses.add(new OrderResponse(order.getId(), date, billAmount, orderedItemResponses));

        when(order.getOrderedItems()).thenReturn(orderedItems);
        when(imageService.getImageLink(image)).thenReturn(imageLink);
        when(order.getDate()).thenReturn(date);
    }

    @Test
    void shouldBeAbleToFetchAllCompletedOrdersWhenCategoryIdAndDateIsGiven() throws NoOrdersFoundException {
        int count = 1;
        Date today = new Date();
        List<Order> orders = new ArrayList<>();
        List<OrderResponse> orderResponses = new ArrayList<>();
        prepareData(count, today, orders, orderResponses);
        BigDecimal revenue = new BigDecimal(100);
        CompletedOrdersResponse expectedCompletedOrdersResponse = new CompletedOrdersResponse(count, revenue, orderResponses);
        when(orderRepository.getAllCompletedOrdersByCategoryAndDate(category.getId(), today, OrderStatus.DELIVERED.name())).thenReturn(orders);
        when(orderRepository.getRevenueOfCompletedOrdersByCategoryIdAndDate(category.getId(), today, OrderStatus.DELIVERED.name())).thenReturn(revenue);

        CompletedOrdersResponse actualCompletedOrdersResponse = orderService.getCompletedOrders(category.getId(), today);

        verify(orderRepository).getAllCompletedOrdersByCategoryAndDate(category.getId(), today, OrderStatus.DELIVERED.name());
        assertThat(actualCompletedOrdersResponse, is(expectedCompletedOrdersResponse));

    }

    @Test
    void shouldThrowNoOrdersFoundExceptionWhenThereAreNoCompletedOrders() {
        long categoryId = category.getId();
        Date today = new Date();

        assertThrows(NoOrdersFoundException.class, () -> orderService.getCompletedOrders(categoryId, today));
    }

    @Test
    void shouldBeAbleToFetchAllActiveOrdersWhenCategoryIdIsGiven() throws NoOrdersFoundException, UserNotFoundException {
        int count = 1;
        Date today = new Date();
        List<Order> orders = new ArrayList<>();
        List<OrderResponse> orderResponses = new ArrayList<>();
        prepareData(count, today, orders, orderResponses);
        ActiveOrderResponse expectedActiveOrderResponse = new ActiveOrderResponse(count, orderResponses);
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        when(orderRepository.getAllActiveOrdersByCategoryId(category.getId(), OrderStatus.PLACED.name())).thenReturn(orders);

        ActiveOrderResponse actualActiveOrders = orderService.getActiveOrders(user.getEmail());

        verify(orderRepository).getAllActiveOrdersByCategoryId(category.getId(), OrderStatus.PLACED.name());
        assertThat(actualActiveOrders, is(expectedActiveOrderResponse));
    }

    @Test
    void shouldThrowNoOrderFoundExceptionWhenThereAreNoActiveOrdersFound() throws UserNotFoundException {
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);

        assertThrows(NoOrdersFoundException.class, () -> orderService.getActiveOrders(user.getEmail()));
    }

    @Test
    void shouldCompleteTheOrderWhenOrderIsNotDelivered() throws OrderNotFoundException, OrderCategoryMismatchException, OrderCancelledException, UserNotFoundException {
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(order.getCategory()).thenReturn(category);
        when(order.getStatus()).thenReturn(OrderStatus.PLACED);
        order.setStatus(OrderStatus.DELIVERED);

        orderService.completeTheOrder(user.getEmail(), order.getId());

        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenInvalidOrderIdIsGiven() {
        assertThrows(OrderNotFoundException.class, () -> orderService.completeTheOrder(user.getEmail(), order.getId()));
    }

    @Test
    void shouldThrowOrderCategoryMismatchExceptionWhenGivenOrderDoesNotBelongToVendorCategory() throws UserNotFoundException {
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(OrderCategoryMismatchException.class, () -> orderService.completeTheOrder(user.getEmail(), order.getId()));
    }

    @Test
    void shouldThrowOrderCancelledExceptionWhenGivenOrderIsCanceled() throws UserNotFoundException {
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(category);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(order.getCategory()).thenReturn(category);
        when(order.getStatus()).thenReturn(OrderStatus.CANCELED);

        assertThrows(OrderCancelledException.class, () -> orderService.completeTheOrder(user.getEmail(), order.getId()));
    }

    @Test
    void shouldBeAbleToOrderAnItemWhenItemIdQuantityAndUserEmailIsGiven() throws UserNotFoundException {
        long quantity = 2;
        Date today = new Date();
        Order savedOrder = new Order(today, user, category);
        savedOrder.AddOrderedItems(new OrderedItem(savedOrder, item, quantity));
        OrderService spiedOrderService = spy(orderService);
        when(spiedOrderService.getCurrentDate()).thenReturn(today);
        when(userPrincipalService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(item.getCategory()).thenReturn(category);

        spiedOrderService.orderAnItem(user.getEmail(), item.getId(), quantity);

        verify(orderRepository).save(savedOrder);
    }
}
