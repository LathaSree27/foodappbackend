package com.tweats.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CategoryItemsResponse {
    private long categoryId;
    private List<ItemResponse> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryItemsResponse that = (CategoryItemsResponse) o;
        return categoryId == that.categoryId && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, items);
    }

}
