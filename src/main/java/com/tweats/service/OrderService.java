package com.tweats.service;

import com.tweats.controller.response.ActiveOrderResponse;
import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.controller.response.OrderResponse;
import com.tweats.controller.response.OrderedItemResponse;
import com.tweats.exceptions.NoOrdersFoundException;
import com.tweats.exceptions.OrderCancelledException;
import com.tweats.exceptions.OrderCategoryMismatchException;
import com.tweats.exceptions.OrderNotFoundException;
import com.tweats.model.*;
import com.tweats.model.constants.OrderStatus;
import com.tweats.repo.CategoryRepository;
import com.tweats.repo.ItemRepository;
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

    private CategoryRepository categoryRepository;

    private ItemRepository itemRepository;

    public CompletedOrdersResponse getCompletedOrders(long categoryId, Date date) throws NoOrdersFoundException {
        List<Order> orders = orderRepository.getAllCompletedOrdersByCategoryAndDate(categoryId, date, OrderStatus.DELIVERED.name());
        int count = orders.size();
        if (count == 0) throw new NoOrdersFoundException();
        BigDecimal revenue = orderRepository.getRevenueOfCompletedOrdersByCategoryIdAndDate(categoryId, date, OrderStatus.DELIVERED.name());
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        createOrderResponse(orders, orderResponses);
        return new CompletedOrdersResponse(count, revenue, orderResponses);

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

    public ActiveOrderResponse getActiveOrders(String vendorEmail) throws NoOrdersFoundException {
        User vendor = userPrincipalService.findUserByEmail(vendorEmail);
        Category category = categoryRepository.findByUserId(vendor.getId());
        List<Order> orders = orderRepository.getAllActiveOrdersByCategoryId(category.getId(), OrderStatus.PLACED.name());
        int count = orders.size();
        if (count == 0) throw new NoOrdersFoundException();
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        createOrderResponse(orders, orderResponses);
        return new ActiveOrderResponse(count, orderResponses);
    }

    public void completeTheOrder(String vendorEmail, long orderId) throws OrderNotFoundException, OrderCategoryMismatchException, OrderCancelledException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException());
        User vendor = userPrincipalService.findUserByEmail(vendorEmail);
        Category category = categoryRepository.findByUserId(vendor.getId());
        if (!category.equals(order.getCategory())) throw new OrderCategoryMismatchException();
        if (order.getStatus().equals(OrderStatus.CANCELED)) throw new OrderCancelledException();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    public void orderAnItem(String userEmail, long itemId, long quantity) {
        User user = userPrincipalService.findUserByEmail(userEmail);
        Item item = itemRepository.findById(itemId).get();
        Date today = getCurrentDate();
        Order order = new Order(today, user, item.getCategory());
        order.AddOrderedItems(new OrderedItem(order, item, quantity));
        orderRepository.save(order);

    }

    public Date getCurrentDate() {
        return new Date();
    }
}
