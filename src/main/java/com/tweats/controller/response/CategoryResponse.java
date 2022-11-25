package com.tweats.controller.response;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private long id;
    private String categoryName;
    private String imageLink;
    private boolean isOpen;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryResponse that = (CategoryResponse) o;
        return id == that.id && isOpen == that.isOpen && categoryName.equals(that.categoryName) && imageLink.equals(that.imageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryName, imageLink, isOpen);
    }
}
