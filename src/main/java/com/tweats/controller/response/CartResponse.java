package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Builder
public class CartResponse {
    private long id;
    private BigDecimal billAmount;
    private List<CartItemResponse> cartItemResponses;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartResponse that = (CartResponse) o;
        return Objects.equals(billAmount, that.billAmount) && Objects.equals(cartItemResponses, that.cartItemResponses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billAmount, cartItemResponses);
    }
}
