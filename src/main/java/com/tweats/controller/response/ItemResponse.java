package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@AllArgsConstructor
@Setter
@Getter
public class ItemResponse {
    private long id;
    private String name;
    private String imageLink;
    private BigDecimal price;
    private boolean isAvailable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemResponse that = (ItemResponse) o;
        return id == that.id && isAvailable == that.isAvailable && name.equals(that.name) && imageLink.equals(that.imageLink) && price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageLink, price, isAvailable);
    }


    public String getImageLink() {
        return imageLink.substring(imageLink.length()-36);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
