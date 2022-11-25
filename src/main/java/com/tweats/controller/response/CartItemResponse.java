package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Builder
public class CartItemResponse {
    private long id;
    private String name;
    private long quantity;
    private BigDecimal price;
    private String imageLink;
    private boolean isAvailable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemResponse that = (CartItemResponse) o;
        return quantity == that.quantity && isAvailable == that.isAvailable && Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(imageLink, that.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity, price, imageLink, isAvailable);
    }
}
