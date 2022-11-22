package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class CompletedOrdersResponse {
    private long count;
    private BigDecimal revenue;
    private List<OrderResponse> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletedOrdersResponse that = (CompletedOrdersResponse) o;
        return count == that.count && revenue.equals(that.revenue) && orders.equals(that.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, revenue, orders);
    }
}
