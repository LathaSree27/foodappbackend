package com.tweats.service;

import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.controller.response.OrderResponse;
import com.tweats.controller.response.OrderedItemResponse;
import com.tweats.model.Item;
import com.tweats.model.Order;
import com.tweats.model.OrderedItem;
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

    public CompletedOrdersResponse getCompletedOrders(long categoryId, Date date) {
        List<Order> orders = orderRepository.getAllCompletedOrdersByCategory(categoryId, date);
        BigDecimal revenue = new BigDecimal(0);
        int count = orders.size();
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            Set<OrderedItem> orderedItems = order.getOrderedItems();
            List<OrderedItemResponse> orderedItemResponses = new ArrayList<>();
            BigDecimal billAmount = new BigDecimal(0);
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
            orderResponses.add(new OrderResponse(order.getId(), order.getDate(), billAmount, orderedItemResponses));
            revenue = revenue.add(billAmount);
        }

        return new CompletedOrdersResponse(count, revenue, orderResponses);

    }
}
