package com.tweats.controller.response;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class VendorCategoryResponse {

    private long id;

    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorCategoryResponse that = (VendorCategoryResponse) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
