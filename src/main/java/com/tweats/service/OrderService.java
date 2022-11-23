package com.tweats.service;

import com.tweats.controller.response.CompletedOrdersResponse;
import com.tweats.controller.response.OrderResponse;
import com.tweats.controller.response.OrderedItemResponse;
import com.tweats.exceptions.NoOrdersFoundException;
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

    public CompletedOrdersResponse getCompletedOrders(long categoryId, Date date) throws NoOrdersFoundException {
        List<Order> orders = orderRepository.getAllOrdersByCategoryDateAndStatus(categoryId, date, true);
        int count = orders.size();
        if (count == 0) throw new NoOrdersFoundException();
        BigDecimal revenue = orderRepository.getRevenueOfCompletedOrdersByCategoryIdAndDate(categoryId, date);
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
}
