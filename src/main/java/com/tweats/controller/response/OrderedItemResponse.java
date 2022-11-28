package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class OrderedItemResponse {
    private long id;
    private String name;
    private long quantity;
    private BigDecimal price;
    private String imageLink;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedItemResponse that = (OrderedItemResponse) o;
        return id == that.id && quantity == that.quantity && name.equals(that.name) && price.equals(that.price) && imageLink.equals(that.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, price, imageLink);
    }
}
