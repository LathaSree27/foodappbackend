package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class ActiveOrderResponse {
    private long count;
    private List<OrderResponse> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveOrderResponse that = (ActiveOrderResponse) o;
        return count == that.count && orders.equals(that.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, orders);
    }
}
