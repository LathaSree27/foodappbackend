package com.tweats.service;

import com.tweats.controller.response.ActiveOrdersResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.controller.response.OrderResponse;
import com.tweats.controller.response.OrderedItemResponse;
import com.tweats.exceptions.*;
import com.tweats.model.*;
import com.tweats.model.constants.OrderStatus;
import com.tweats.repo.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class OrderService {
    private OrderRepository orderRepository;

    private ImageService imageService;

    private UserPrincipalService userPrincipalService;

    private CategoryService categoryService;

    private ItemService itemService;

    private CartService cartService;

    public CompletedOrdersResponse getCompletedOrders(long categoryId, Date date) throws NoOrdersFoundException {
        List<Order> orders = orderRepository.getAllCompletedOrdersByCategoryAndDate(categoryId, date, OrderStatus.DELIVERED.name());
        int count = orders.size();
        if (count == 0) throw new NoOrdersFoundException();
        BigDecimal revenue = orderRepository.getRevenueOfCompletedOrdersByCategoryIdAndDate(categoryId, date, OrderStatus.DELIVERED.name());
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        createOrderResponse(orders, orderResponses);
        return getCompletedOrdersResponse(count, revenue, orderResponses);
    }

    private void createOrderResponse(List<Order> orders, ArrayList<OrderResponse> orderResponses) {
        for (Order order : orders) {
            Set<OrderedItem> orderedItems = order.getOrderedItems();
            List<OrderedItemResponse> orderedItemResponses = new ArrayList<>();
            BigDecimal billAmount = new BigDecimal(0);
            billAmount = getBillAmount(orderedItems, orderedItemResponses, billAmount);
            orderResponses.add(new OrderResponse(order.getId(), order.getDate(), billAmount, orderedItemResponses));
        }
    }

    private BigDecimal getBillAmount(Set<OrderedItem> orderedItems, List<OrderedItemResponse> orderedItemResponses, BigDecimal billAmount) {
        for (OrderedItem orderedItem : orderedItems) {
            Item item = orderedItem.getItem();
            orderedItemResponses.add(
                    new OrderedItemResponse(
                            item.getId(),
                            item.getName(),
                            orderedItem.getQuantity(),
                            item.getPrice(),
                            imageService.getImageLink(item.getImage())
                    )
            );
            billAmount = billAmount.add(item.getPrice().multiply(BigDecimal.valueOf(orderedItem.getQuantity())));
        }
        return billAmount;
    }

    private CompletedOrdersResponse getCompletedOrdersResponse(int count, BigDecimal revenue, List<OrderResponse> orders) {
        return CompletedOrdersResponse.builder()
                .count(count)
                .revenue(revenue)
                .orders(orders)
                .build();
    }

    private ActiveOrdersResponse getActiveOrdersResponse(int count, List<OrderResponse> orders) {
        return ActiveOrdersResponse.builder()
                .count(count)
                .orders(orders)
                .build();
    }

    public ActiveOrdersResponse getActiveOrders(String vendorEmail) throws NoOrdersFoundException, UserNotFoundException, NoCategoryFoundException {
        Category category = categoryService.getCategory(vendorEmail);
        List<Order> orders = orderRepository.getAllActiveOrdersByCategoryId(category.getId(), OrderStatus.PLACED.name());
        int count = orders.size();
        if (count == 0) throw new NoOrdersFoundException();
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        createOrderResponse(orders, orderResponses);
        return getActiveOrdersResponse(count, orderResponses);
    }

    public void completeTheOrder(String vendorEmail, long orderId) throws OrderNotFoundException, OrderCategoryMismatchException, OrderCancelledException, UserNotFoundException, NoCategoryFoundException, OrderAlreadyCompletedException {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        Category category = categoryService.getCategory(vendorEmail);
        if (!category.equals(order.getCategory())) throw new OrderCategoryMismatchException();
        if (order.getStatus().equals(OrderStatus.CANCELED)) throw new OrderCancelledException();
        if (order.getStatus().equals(OrderStatus.DELIVERED)) throw new OrderAlreadyCompletedException();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    public void orderAnItem(String userEmail, long itemId, long quantity) throws UserNotFoundException, ItemDoesNotExistException {
        User user = userPrincipalService.findUserByEmail(userEmail);
        Item item = itemService.getItem(itemId);
        Date today = getCurrentDate();
        Order order = new Order(today, user, item.getCategory());
        order.addOrderedItem(new OrderedItem(order, item, quantity));
        orderRepository.save(order);
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public void placeOrder(String userEmail, long categoryId) throws UserNotFoundException, NoCategoryFoundException, EmptyCartException {
        User user = userPrincipalService.findUserByEmail(userEmail);
        Cart cart = cartService.getCart(user.getId(), categoryId);
        Set<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) throw new EmptyCartException();
        Order order = new Order(getCurrentDate(), user, cart.getCategory());
        for (CartItem cartItem : cartItems) {
            Item item = cartItem.getItem();
            if (item.isAvailable()) order.addOrderedItem(new OrderedItem(order, item, cartItem.getQuantity()));
        }
        if (order.getOrderedItems().isEmpty()) throw new EmptyCartException();
        orderRepository.save(order);
        cartService.emptyCart(cart);
    }
}
