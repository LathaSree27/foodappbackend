package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class ItemListResponse {
    private long categoryId;
    private List<ItemResponse> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemListResponse that = (ItemListResponse) o;
        return categoryId == that.categoryId && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, items);
    }

    @Override
    public String toString() {
        return "{" +
                "categoryId=" + categoryId +
                ", items=" + items +
                '}';
    }
}
