package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class CartResponse {
    private long id;
    private BigDecimal billAmount;
    private List<CartItemResponse> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartResponse that = (CartResponse) o;
        return id == that.id && billAmount.equals(that.billAmount) && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, billAmount, items);
    }
}
